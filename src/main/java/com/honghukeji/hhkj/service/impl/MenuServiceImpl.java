package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honghukeji.hhkj.dao.MenuDAO;
import com.honghukeji.hhkj.entity.Menu;
import com.honghukeji.hhkj.service.MenuService;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuDAO, Menu> implements MenuService {
}
