package com.honghukeji.hhkj.helper;

import com.honghukeji.hhkj.objs.QiniuEntity;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class   Qiniu {
    private String AK;
    private String SK;
    private String bucket;
    private String domain;
    public Qiniu(QiniuEntity qiniuEntity){
        this.AK=qiniuEntity.getAk();
        this.SK=qiniuEntity.getSk();
        this.bucket=qiniuEntity.getBucket();
        this.domain=qiniuEntity.getDomain();
    }
    /**
     * 获取七牛上传的token
     * @param key 文件key
     * @return
     */
    public String getToken(String key){
        System.out.println("key:"+key);
        Auth auth=Auth.create(AK,SK);
        return auth.uploadToken(bucket, key);
    }
    public String getToken(){
        Auth auth=Auth.create(AK,SK);
        return auth.uploadToken(bucket);
    }
    //上传文件
    public void  uploadFile(byte[] uploadBytes,String key){
        Configuration cfg=new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        String upToken=getToken(key);
        System.out.println("上传token:"+upToken);
        try {
            Response response = uploadManager.put(uploadBytes, key, upToken);
            //解析上传成功的结果
            System.out.println("上传结果:"+response.bodyString());
        } catch (QiniuException ex) {
            System.out.println("上传失败");
            Response r = ex.response;
            System.err.println(r.toString());
//            try {
//                System.err.println(r.bodyString());
//            } catch (QiniuException ex2) {
//                //ignore
//            }
        }
    }
    //删除文件
    public void delFile(String key){
        Configuration cfg=new Configuration(Region.region2());
        Auth auth=Auth.create(AK,SK);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            System.out.println("七牛文件删除成功!");
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}
