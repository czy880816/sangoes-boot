package com.sangoes.boot.uc.modules.admin.service;

import com.sangoes.boot.uc.modules.admin.dto.AuthDto;
import com.sangoes.boot.uc.modules.admin.entity.SysAuth;
import com.sangoes.boot.common.msg.Result;
import com.sangoes.boot.common.service.IBaseService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jerrychir
 * @since 2018-11-10
 */
public interface ISysAuthService extends IBaseService<SysAuth> {

    /**
     * 添加权限
     * 
     * @param authDto
     * @return
     */
    Result<String> addAuth(AuthDto authDto);

}
