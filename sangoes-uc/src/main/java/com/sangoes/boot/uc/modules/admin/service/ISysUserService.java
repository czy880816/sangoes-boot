package com.sangoes.boot.uc.modules.admin.service;

import com.sangoes.boot.common.msg.Result;
import com.sangoes.boot.common.service.IBaseService;
import com.sangoes.boot.common.utils.page.PageData;
import com.sangoes.boot.uc.modules.admin.dto.SignInDto;
import com.sangoes.boot.uc.modules.admin.dto.SignUpDto;
import com.sangoes.boot.uc.modules.admin.dto.UserDto;
import com.sangoes.boot.uc.modules.admin.entity.SysUser;
import com.sangoes.boot.uc.modules.admin.vo.UserDetailsVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author jerrychir
 * @since 2018-10-29
 */
public interface ISysUserService extends IBaseService<SysUser> {
    /**
     * 根据手机号码注册
     *
     * @param signUpDto
     */
    Result<String> signUpByMobile(SignUpDto signUpDto);

    /**
     * 根据手机号码登陆
     *
     * @param signInDto
     * @return
     */
    Result<String> signinByMobile(SignInDto signInDto);

    /**
     * 根据username查询UserDeatilsVo
     *
     * @param username
     * @return
     */
    UserDetailsVo selectUserDetailsByUsername(String username);

    /**
     * 根据mobile查询UserDetailsVo
     *
     * @param mobile
     * @return
     */
    UserDetailsVo selectUserDetailsByMobile(String mobile);

    /**
     * 根据用户名登录
     */
    Result<String> signinByAccount(SignInDto signInDto);

    /**
     * 添加用户
     *
     * @param userDto
     * @return
     */
    Result<String> addUser(UserDto userDto);

    /**
     * 分页获取用户
     *
     * @param params
     * @return
     */
    Result<PageData<SysUser>> selectUserPage(Map<String, Object> params);

    /**
     * 查询用户绑定的角色
     *
     * @param id
     * @return
     */
    Result<Map<String, Object>> infoBindRole(Long id);

    /**
     * 绑定角色
     *
     * @param userDto
     * @return
     */
    Result<String> bindRole(UserDto userDto);

    /**
     * 获取当前用户信息
     *
     * @param userId
     * @return
     */
    SysUser userInfo(Long userId);

    /**
     * 用户上传头像
     *
     * @param file
     * @return
     */
    String uploadAvatar(Long userId, MultipartFile file);
}
