package com.honghukeji.hhkj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honghukeji.hhkj.controllers.admin.admin.dto.AdminListDto;
import com.honghukeji.hhkj.controllers.admin.admin.dto.AdminLogDto;
import com.honghukeji.hhkj.controllers.admin.admin.vo.AdminListVo;
import com.honghukeji.hhkj.controllers.admin.admin.vo.AdminLogVo;
import com.honghukeji.hhkj.controllers.admin.admin.vo.GetSearchAdminListVo;
import com.honghukeji.hhkj.entity.Admin;
import com.honghukeji.hhkj.objs.SelectVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminDAO  extends BaseMapper<Admin> {

    Admin findByUsername(String username);

    List<AdminListVo> getAdminList(AdminListDto req);

    List<AdminLogVo> getAdminLog(AdminLogDto req);

    List<SelectVo> getSearchAdminList();
}
