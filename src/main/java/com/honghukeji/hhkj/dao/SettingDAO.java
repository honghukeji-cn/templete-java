package com.honghukeji.hhkj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honghukeji.hhkj.controllers.admin.setting.vo.GetSettingListVo;
import com.honghukeji.hhkj.entity.Setting;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SettingDAO extends BaseMapper<Setting> {
    List<GetSettingListVo> getSettingList();
}
