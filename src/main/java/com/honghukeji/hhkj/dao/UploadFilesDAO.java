package com.honghukeji.hhkj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honghukeji.hhkj.controllers.admin.setting.vo.GetFileListVo;
import com.honghukeji.hhkj.controllers.admin.setting.vo.UploadLog;
import com.honghukeji.hhkj.entity.UploadFiles;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UploadFilesDAO extends BaseMapper<UploadFiles> {
    List<GetFileListVo> selectByPid(Map map);

    List<UploadLog> getImgList();
}
