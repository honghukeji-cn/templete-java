package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honghukeji.hhkj.dao.AdminLogDAO;
import com.honghukeji.hhkj.entity.AdminLog;
import com.honghukeji.hhkj.service.AdminLogService;
import org.springframework.stereotype.Service;

@Service
public class AdminLogServiceImpl extends ServiceImpl<AdminLogDAO, AdminLog> implements AdminLogService {
}
