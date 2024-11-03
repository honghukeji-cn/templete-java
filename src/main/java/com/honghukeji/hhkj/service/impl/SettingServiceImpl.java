package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honghukeji.hhkj.dao.SettingDAO;
import com.honghukeji.hhkj.entity.Setting;
import com.honghukeji.hhkj.service.SettingService;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl extends ServiceImpl<SettingDAO, Setting> implements SettingService {
}
