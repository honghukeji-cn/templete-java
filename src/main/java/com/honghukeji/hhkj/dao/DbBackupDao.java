package com.honghukeji.hhkj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honghukeji.hhkj.controllers.admin.backup.dto.DbBackUpListDto;
import com.honghukeji.hhkj.controllers.admin.backup.vo.DbBackupVo;
import com.honghukeji.hhkj.entity.DbBackup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
/**
 * 数据库备份记录(DbBackup)表数据库访问层
 *
 * @author makejava
 * @since 2024-11-02 14:20:48
 */
public interface DbBackupDao extends BaseMapper<DbBackup> {

    List<DbBackupVo> getList(DbBackUpListDto dto);
}

