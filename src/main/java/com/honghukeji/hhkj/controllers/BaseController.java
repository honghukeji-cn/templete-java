package com.honghukeji.hhkj.controllers;


import com.honghukeji.hhkj.service.AuthConfigService;
import com.honghukeji.hhkj.service.DbBackupService;
import com.honghukeji.hhkj.service.impl.AsyncServiceImpl;
import com.honghukeji.hhkj.helper.Redis;
import com.honghukeji.hhkj.service.AdminLogService;
import com.honghukeji.hhkj.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {
    @Autowired
    protected AdminServiceImpl adminService;
    @Autowired
    protected SettingServiceImpl settingService;
    @Autowired
    protected Redis redis;
    @Autowired
    protected MenuServiceImpl menuService;
    @Autowired
    protected RoleServiceImpl roleService;
    @Autowired
    protected UploadSetServiceImpl uploadSetService;
    @Autowired
    protected UploadFilesServiceImpl uploadFilesService;
    @Autowired
    protected AdminLogService adminLogService;
    @Autowired
    protected AuthConfigService authConfigService;
    @Autowired
    protected DbBackupService dbBackupService;
    @Autowired
    protected AsyncServiceImpl asyncService;


}
