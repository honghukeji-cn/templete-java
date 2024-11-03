package com.honghukeji.hhkj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.honghukeji.hhkj.entity.AdminLog;
import com.honghukeji.hhkj.entity.UploadFiles;
import com.honghukeji.hhkj.entity.UploadSet;
import com.honghukeji.hhkj.helper.AliOSS;
import com.honghukeji.hhkj.helper.Qiniu;
import com.honghukeji.hhkj.helper.TXCos;
import com.honghukeji.hhkj.objs.AliOssEntity;
import com.honghukeji.hhkj.objs.QiniuEntity;
import com.honghukeji.hhkj.objs.TxCosEntity;
import com.honghukeji.hhkj.service.AsyncService;
import com.honghukeji.hhkj.service.impl.AdminLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Service
public class AsyncServiceImpl implements AsyncService {
    @Value("${file.upload-path}")
    private String uploadPath;
    @Autowired
    AdminLogServiceImpl adminLogService;

    @Override
    @Async
    public void addAdminLog(AdminLog adminLog) {
        System.out.println("执行异步任务");
        adminLogService.save(adminLog);
    }

    @Override
    @Async
    public void removeFile(UploadFiles file, UploadSet set) {
        System.out.println("异步执行");
        int domain=file.getDomain();
        String key=file.getKey();
        if (domain > 0 && domain < 4) {
            if (domain == 1) {
                //七牛文件
                QiniuEntity qiniuEntity = JSON.toJavaObject(JSONObject.parseObject(set.getQiniu()), QiniuEntity.class);
                Qiniu qiniu = new Qiniu(qiniuEntity);
                qiniu.delFile(key);
            } else if (domain == 2) {
                //阿里OSS
                AliOssEntity aliOssEntity = JSON.toJavaObject(JSONObject.parseObject(set.getAlioss()), AliOssEntity.class);
                AliOSS aliOSS = new AliOSS(aliOssEntity);
                aliOSS.delFile(key);
            } else if (domain == 3) {
                //腾讯cos
                TxCosEntity txCosEntity = JSON.toJavaObject(JSONObject.parseObject(set.getTxcos()), TxCosEntity.class);
                TXCos txCos = new TXCos(txCosEntity);
                txCos.delFile(key);
            }
        }
        if (domain == 4) {
            //删除本地目录文件
            String url = uploadPath + file.getName();
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
        }

    }
}
