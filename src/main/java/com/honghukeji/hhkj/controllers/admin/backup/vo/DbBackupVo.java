package com.honghukeji.hhkj.controllers.admin.backup.vo;

import com.honghukeji.hhkj.entity.DbBackup;
import lombok.Data;

@Data
public class DbBackupVo extends DbBackup {
    private String admin_name;
}
