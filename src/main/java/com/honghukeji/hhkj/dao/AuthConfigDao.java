package com.honghukeji.hhkj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honghukeji.hhkj.entity.AuthConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 登录安全配置(AuthConfig)表数据库访问层
 *
 * @author makejava
 * @since 2024-11-01 16:16:00
 */
public interface AuthConfigDao extends BaseMapper<AuthConfig> {

}

