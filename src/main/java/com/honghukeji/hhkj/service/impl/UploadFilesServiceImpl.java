package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honghukeji.hhkj.service.AsyncService;
import com.honghukeji.hhkj.dao.UploadFilesDAO;
import com.honghukeji.hhkj.entity.UploadFiles;
import com.honghukeji.hhkj.exception.ErrorException;
import com.honghukeji.hhkj.service.UploadFilesService;
import com.honghukeji.hhkj.service.UploadSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadFilesServiceImpl extends ServiceImpl<UploadFilesDAO, UploadFiles> implements UploadFilesService {

    @Autowired
    protected AsyncService asyncService;
    @Autowired
    protected UploadSetService uploadSetService;
    public void removeFileById(int id) {
        UploadFiles file=getById(id);
        if(file==null)
        {
            throw new ErrorException("文件不存在!");
        }
                //判断如果文件为文件夹形式
        if(file.getType()==8){
            //文件夹-判断下面是否还有文件
            int num=count(new QueryWrapper<UploadFiles>().eq("pid",id));
            if(num>0)
            {
                throw new ErrorException("请先删除文件夹中的文件!");
            }
        }
        //删除文件
        if(removeById(id)) {
            //子线程删除第三方文件
           asyncService.removeFile(file, uploadSetService.getById(1));
           System.out.println("同步");
        }
    }
}
