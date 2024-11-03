package com.honghukeji.hhkj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honghukeji.hhkj.controllers.admin.backup.dto.DbBackUpListDto;
import com.honghukeji.hhkj.controllers.admin.backup.vo.DbBackupVo;
import com.honghukeji.hhkj.entity.DbBackup;
import com.honghukeji.hhkj.objs.PageVo;

/**
 * 数据库备份记录(DbBackup)表服务接口
 *
 * @author makejava
 * @since 2024-11-02 14:20:48
 */
public interface DbBackupService extends IService<DbBackup> {
    void backup(Integer admin_id);

    void restoreDbById(Integer id);

    PageVo<DbBackupVo> getList(DbBackUpListDto dto);
}

