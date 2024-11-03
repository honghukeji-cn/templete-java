package com.honghukeji.hhkj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honghukeji.hhkj.controllers.admin.admin.vo.AuthMenuVo;
import com.honghukeji.hhkj.controllers.admin.menu.vo.GetMenuListVo;
import com.honghukeji.hhkj.controllers.admin.menu.vo.GetMenusByPidVo;
import com.honghukeji.hhkj.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuDAO extends BaseMapper<Menu> {
    List<AuthMenuVo> getAuthMenuList(int i, String ids);

    List<GetMenusByPidVo> getMenusByPid(int pid);

    List<GetMenuListVo> getMenus(int i);
}
