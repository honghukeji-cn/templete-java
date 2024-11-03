package com.honghukeji.hhkj.service;

import com.honghukeji.hhkj.entity.AdminLog;
import com.honghukeji.hhkj.entity.UploadFiles;
import com.honghukeji.hhkj.entity.UploadSet;

public interface AsyncService {
    void addAdminLog(AdminLog adminLog);
    void removeFile(UploadFiles file, UploadSet set);
}
