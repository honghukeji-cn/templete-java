package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honghukeji.hhkj.dao.AdminDAO;
import com.honghukeji.hhkj.entity.Admin;
import com.honghukeji.hhkj.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminDAO, Admin> implements AdminService {
}
