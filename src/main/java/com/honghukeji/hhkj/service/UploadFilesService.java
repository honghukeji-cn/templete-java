package com.honghukeji.hhkj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honghukeji.hhkj.entity.UploadFiles;

public interface UploadFilesService extends IService<UploadFiles> {
     void removeFileById(int id);//根据文件ID删除文件或者文件夹
}
