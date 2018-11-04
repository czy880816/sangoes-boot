package com.sangoes.boot.uc.modules.admin.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangoes.boot.common.exception.HandleErrorException;
import com.sangoes.boot.common.msg.Result;
import com.sangoes.boot.uc.constants.CaptchaConstants;
import com.sangoes.boot.uc.constants.RSAConstants;
import com.sangoes.boot.uc.modules.admin.dto.SignInDto;
import com.sangoes.boot.uc.modules.admin.dto.SignUpDto;
import com.sangoes.boot.uc.modules.admin.entity.SysUser;
import com.sangoes.boot.uc.modules.admin.mapper.SysUserMapper;
import com.sangoes.boot.uc.modules.admin.service.ISysUserService;
import com.sangoes.boot.uc.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author jerrychir
 * @since 2018-10-29
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 根据手机号码注册
     *
     * @param signUpDto
     */
    @Override
    public Result signUpByMobile(SignUpDto signUpDto) {
        // 验证码
        String captchaConstant = CaptchaConstants.CAPTCHA_MOBILE_SMS + signUpDto.getMobile();
        // 检测是否有mobile对应的redis缓存
        boolean hasKey = redisTemplate.hasKey(captchaConstant).booleanValue();
        if (!hasKey) {
            throw new HandleErrorException("验证码不存在或过期");
        }
        // 删除验证码
        redisTemplate.delete(captchaConstant);
        // 验证手机号码是否被注册
        SysUser user = baseMapper
                .selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getMobile, signUpDto.getMobile()));
        if (!ObjectUtil.isNull(user)) {
            throw new HandleErrorException("手机号码已注册");
        }
        // 验证用户名是否被存在
        SysUser userName = baseMapper
                .selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUsername, signUpDto.getUsername()));
        if (!ObjectUtil.isNull(userName)) {
            throw new HandleErrorException("用户名已注册");
        }
        // 从缓存中获取privateKey
        String privateKey = String
                .valueOf(redisTemplate.opsForValue().get(RSAConstants.MOBILE_RSA_PRIVATE_KEY + signUpDto.getMobile()));
        String publicKey = String
                .valueOf(redisTemplate.opsForValue().get(RSAConstants.MOBILE_RSA_PUBLIC_KEY + signUpDto.getMobile()));
        // 解密密码
        AsymmetricCrypto crypto = new AsymmetricCrypto(AsymmetricAlgorithm.RSA, privateKey, publicKey);
        String password = StrUtil.str(crypto.decryptFromBase64(signUpDto.getPassword(), KeyType.PrivateKey),
                CharsetUtil.CHARSET_UTF_8);

        // 创建user
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(signUpDto, sysUser);
        // 设置随机真实姓名
        sysUser.setRealName(RandomUtil.randomString(8));
        // 加密密码
        sysUser.setPassword(passwordEncoder.encode(password));
        // 写入数据库
        boolean save = this.save(sysUser);
        if (!save) {
            throw new HandleErrorException("注册失败");
        }
        return Result.success("注册成功");
    }

    /**
     * 根据手机号码登陆
     *
     * @param signInDto
     * @return
     */
    @Override
    public Result signinByMobile(SignInDto signInDto) {
        // 根据mobile查询sys user
        SysUser userDB = this
                .getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getMobile, signInDto.getMobile()));
        // 判断是否存在
        if (ObjectUtil.isNull(userDB)) {
            throw new HandleErrorException("此号码没有注册,请注册");
        }
        // 待验证验证码
        String captcha = signInDto.getCaptcha();
        // redis中的验证码
        String captchaConstant = CaptchaConstants.CAPTCHA_MOBILE_SMS + signInDto.getMobile();
        // 检测是否有mobile对应的redis缓存
        boolean hasKey = redisTemplate.hasKey(captchaConstant).booleanValue();
        if (!hasKey) {
            throw new HandleErrorException("验证码不存在或过期");
        }
        // 获取redis中的验证码
        String captchaRedis = String.valueOf(redisTemplate.opsForValue().get(captchaConstant));
        // 判断验证码是否相同
        if (!StringUtils.equals(captcha, captchaRedis)) {
            throw new HandleErrorException("验证码错误");
        }
        // try {
        // 改变signinType
        userDB.setLoginType(signInDto.getSigninType());
        // 更新
        boolean flag = this.updateById(userDB);
        if (!flag) {
            throw new HandleErrorException("登录失败");
        }
        // 登录
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDB.getUsername(), userDB.getPassword()));
        // 创建token
        return Result.success(jwtTokenProvider.createToken(userDB.getUsername()), "登录成功");
        // } catch (AuthenticationException e) {
        // throw new HandleErrorException("登陆失败");
        // }

    }
}
