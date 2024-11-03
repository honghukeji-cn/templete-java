package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honghukeji.hhkj.dao.RoleDAO;
import com.honghukeji.hhkj.entity.Role;
import com.honghukeji.hhkj.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleDAO, Role> implements RoleService {
}
