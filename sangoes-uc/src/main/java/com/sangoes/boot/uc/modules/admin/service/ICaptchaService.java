package com.sangoes.boot.uc.modules.admin.service;

/**
 * Copyright (c) 2018
 * 验证码 服务类
 *
 * @author jerrychir
 * @date 2018/10/30 8:12 PM
 */
public interface ICaptchaService {
    /**
     * 根据手机号码发送验证码并返回公钥
     *
     * @param mobile 手机号码
     * @return 公钥
     */
    String sendCaptchaBySms(String mobile);
}
