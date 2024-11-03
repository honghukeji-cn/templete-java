package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honghukeji.hhkj.dao.AuthConfigDao;
import com.honghukeji.hhkj.entity.AuthConfig;
import com.honghukeji.hhkj.service.AuthConfigService;
import org.springframework.stereotype.Service;

/**
 * 登录安全配置(AuthConfig)表服务实现类
 *
 * @author makejava
 * @since 2024-11-01 16:16:00
 */
@Service
public class AuthConfigServiceImpl extends ServiceImpl<AuthConfigDao, AuthConfig> implements AuthConfigService {

}

