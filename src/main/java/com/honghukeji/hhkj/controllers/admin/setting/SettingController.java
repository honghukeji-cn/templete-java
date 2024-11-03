package com.honghukeji.hhkj.controllers.admin.setting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.honghukeji.hhkj.HhkjPlusApplication;
import com.honghukeji.hhkj.annotation.CheckToken;
import com.honghukeji.hhkj.controllers.BaseController;
import com.honghukeji.hhkj.controllers.admin.setting.dto.*;
import com.honghukeji.hhkj.controllers.admin.setting.vo.GetFileListVo;
import com.honghukeji.hhkj.controllers.admin.setting.vo.GetSettingListVo;
import com.honghukeji.hhkj.controllers.admin.setting.vo.GetUploadConfigVo;
import com.honghukeji.hhkj.entity.AuthConfig;
import com.honghukeji.hhkj.entity.Setting;
import com.honghukeji.hhkj.entity.UploadFiles;
import com.honghukeji.hhkj.entity.UploadSet;
import com.honghukeji.hhkj.helper.AliOSS;
import com.honghukeji.hhkj.helper.Helper;
import com.honghukeji.hhkj.helper.Qiniu;
import com.honghukeji.hhkj.helper.TXCos;
import com.honghukeji.hhkj.objs.*;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/setting")
@CheckToken
public class SettingController extends BaseController {
    @Value("${file.upload-path}")
    private String uploadPath;
    @Value("${file.upload-url}")
    private String uploadUrl;
    //获取上传配置信息
    @RequestMapping("/getUploadConfig")
    public JSONResult<GetUploadConfigVo> getUploadConfig(){
        //获取配置信息
        UploadSet set=uploadSetService.getBaseMapper().selectById(1);
        GetUploadConfigVo vo=new GetUploadConfigVo();
        vo.setVisible(set.getVisible());
        QiniuEntity qiniuEntity= JSON.toJavaObject(JSONObject.parseObject(set.getQiniu()),QiniuEntity.class);
        vo.setQiniu(qiniuEntity);
        AliOssEntity aliOssEntity=JSON.toJavaObject(JSONObject.parseObject(set.getAlioss()),AliOssEntity.class);
        vo.setAlioss(aliOssEntity);
        TxCosEntity txCosEntity=JSON.toJavaObject(JSONObject.parseObject(set.getTxcos()),TxCosEntity.class);
        vo.setTxcos(txCosEntity);
        return JSONResult.success(vo);
    }
    //保存本地配置
    @RequestMapping("/saveLocal")
    @CheckToken(routes = {"/admin/setting/getUploadConfig"})
    public JSONResult<EmptyVo> saveLocal(@RequestBody QiniuDto req){
        UploadSet uploadSet=new UploadSet();
        uploadSet.setId(1);
        uploadSet.setVisible(req.getVisible());
        if(uploadSetService.updateById(uploadSet)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //保存七牛配置
    @RequestMapping("/saveQiniu")
    @CheckToken(routes = {"/admin/setting/getUploadConfig"})
    public JSONResult<EmptyVo> saveQiniu(@RequestBody QiniuDto req){
        QiniuEntity qiniuEntity=(QiniuEntity)req;
        String qiniu=JSON.toJSONString(qiniuEntity);
        UploadSet uploadSet=new UploadSet();
        uploadSet.setId(1);
        uploadSet.setVisible(req.getVisible());
        uploadSet.setQiniu(qiniu);
        if(uploadSetService.updateById(uploadSet)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }

    //保存阿里oss
    @RequestMapping("/saveAlioss")
    @CheckToken(routes = {"/admin/setting/getUploadConfig"})
    public JSONResult<EmptyVo> saveAlioss(@RequestBody AliOssDto req){
        AliOssEntity aliOssEntity=(AliOssEntity) req;
        String alioss=JSON.toJSONString(aliOssEntity);
        UploadSet uploadSet=new UploadSet();
        uploadSet.setId(1);
        uploadSet.setVisible(req.getVisible());
        uploadSet.setAlioss(alioss);
        if(uploadSetService.updateById(uploadSet)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //保存腾讯cos
    @RequestMapping("/saveTxcos")
    @CheckToken(routes = {"/admin/setting/getUploadConfig"})
    public JSONResult<EmptyVo> saveTxcos(@RequestBody TxCosDto req){
        TxCosEntity txCosEntity=(TxCosEntity) req;
        String txcos=JSON.toJSONString(txCosEntity);
        UploadSet uploadSet=new UploadSet();
        uploadSet.setId(1);
        uploadSet.setVisible(req.getVisible());
        uploadSet.setTxcos(txcos);
        if(uploadSetService.updateById(uploadSet)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //新增配置
    @RequestMapping("/addSetting")
    public JSONResult<EmptyVo> addSetting(@RequestBody AddSetting req){
        Setting setting=new Setting(
            0,req.getType(),req.getTitle(),req.getValue(),req.getCanDel()
        );
        if(settingService.save(setting)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //编辑配置
    @RequestMapping("/editSetting")
    public JSONResult<EmptyVo> editSetting(@RequestBody EditSettingDto req){
        Setting setting=new Setting(
                req.getId(),req.getType(),req.getTitle(),req.getValue(),0
        );
        if(settingService.updateById(setting)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //删除配置
    @RequestMapping("delSetting")
    public JSONResult<EmptyVo> delSetting(@RequestBody DelSettingDto req){
        Setting setting=settingService.getById(req.getId());
        if(setting==null){
            return JSONResult.error("信息不存在");
        }
        if(setting.getCanDel()!=1){
            return JSONResult.error("此配置无法删除!");
        }
        if(settingService.removeById(req.getId())){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //配置列表
    @RequestMapping("/settingList")
    public JSONResult<PageVo<GetSettingListVo>> settingList(@RequestBody PageDto req){
        if(!req.getOrderBy().equals("")){
            req.setOrderBy("id "+req.getOrderBy());
        }
        PageHelper.startPage(req.getPage(),req.getSize(),req.getOrderBy());
        List<GetSettingListVo> vos= settingService.getBaseMapper().getSettingList();
        PageInfo<GetSettingListVo> pageInfo=new PageInfo<>(vos);
        PageVo<GetSettingListVo> pageVo=new PageVo<>();
        pageVo.setAll((int) pageInfo.getTotal());
        pageVo.setDatas(vos);
        return JSONResult.success(pageVo);
    }

    //获取当前的上传配置信息
    @RequestMapping("/getUploadToken")
    @CheckToken(checkAuth = false)
    public JSONResult getUploadToken(){
        UploadSet set=uploadSetService.getBaseMapper().selectById(1);
        Map json=new HashMap();
        if(set.getVisible()==1){
            //七牛
            QiniuEntity qiniuEntity= JSON.toJavaObject(JSONObject.parseObject(set.getQiniu()),QiniuEntity.class);
            Qiniu qiniu=new Qiniu(qiniuEntity);
            json.put("token",qiniu.getToken());
            json.put("domain",qiniu.getDomain());
        }else if(set.getVisible()==2){
            //阿里oss
            AliOssEntity aliOssEntity=JSON.toJavaObject(JSONObject.parseObject(set.getAlioss()),AliOssEntity.class);
            AliOSS aliOSS=new AliOSS(aliOssEntity);
            json=aliOSS.getToken();
        }else if(set.getVisible()==3){
            //腾讯cos
            TxCosEntity txCosEntity=JSON.toJavaObject(JSONObject.parseObject(set.getTxcos()),TxCosEntity.class);
            TXCos txCos=new TXCos(txCosEntity);
            json=txCos.getTempKey();
        }else if(set.getVisible()==4){
            //本地上传
            String uptoken=Helper.getRadomStr(32);
            redis.set(uptoken,"1");
            redis.expireAt(uptoken,Helper.addSeconds(Helper.getDaDate(),3600*24));
            json.put("uptoken",uptoken);
            json.put("uploadUrl",uploadUrl+"upload");
        }else{
            return JSONResult.error("未配置上传信息");
        }
        json.put("visible",set.getVisible());
        return JSONResult.success(json);
    }
    //新增文件
    @RequestMapping("/addFile")
    @CheckToken(checkAuth = false)
    public JSONResult<EmptyVo> addFile(@RequestBody @Valid AddFileDto req, BindingResult result){
        Helper.checkError(result);
        UploadFiles files=new UploadFiles(
                0,req.getDomain(),req.getType(), req.getName(), req.getName(), req.getUrl(), Helper.getDaDate(), req.getPid()
        );
        if(uploadFilesService.save(files)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //删除文件
    @RequestMapping("/delFile")
    @CheckToken(checkAuth = false)
    public JSONResult<EmptyVo> delFile(@RequestBody DelFileDto req){
        uploadFilesService.removeFileById(req.getId());
        return JSONResult.success();
    }
    //获取文件列表
    @RequestMapping("/getFileList")
    @CheckToken(checkAuth = false)
    public JSONResult<PageVo<GetFileListVo>> getFileList(@RequestBody GetFileListDto req){
        PageHelper.startPage(req.getPage(),req.getSize(),"atime desc");
        Map map=new HashMap();
        map.put("pid",req.getPid());
        map.put("name",req.getName());
        map.put("type",req.getType());
        List<GetFileListVo>vos=uploadFilesService.getBaseMapper().selectByPid(map);
        PageInfo<GetFileListVo> pageInfo=new PageInfo<>(vos);
        PageVo<GetFileListVo> pageVo=new PageVo<GetFileListVo>();
        pageVo.setAll((int) pageInfo.getTotal());
        pageVo.setDatas(vos);
        return JSONResult.success(pageVo);
    }

    //设置安全规则
    @RequestMapping("/setAuthConfig")
    public JSONResult<EmptyVo> setAuthConfig(@RequestBody SetAuthConfigDto dto)
    {

        AuthConfig authConfig=authConfigService.getById(1);
        boolean reload=!authConfig.getBakup_db_cron().equals(dto.getBakup_db_cron()) || !authConfig.getAuto_backup().equals(dto.getAuto_backup());
        BeanUtils.copyProperties(dto,authConfig);
        authConfig.setAtime(Helper.getDaDate());
        authConfigService.updateById(authConfig);
        if(reload)
        {
            new Runnable(){
                @SneakyThrows
                @Override
                public void run() {

                    ApplicationArguments args = HhkjPlusApplication.context.getBean(ApplicationArguments.class);
                    Thread thread = new Thread(() -> {
                        System.out.println("springboot restart...");
                        HhkjPlusApplication.context.close();
                        HhkjPlusApplication.context = SpringApplication.run(HhkjPlusApplication.class, args.getSourceArgs());
                    });
                    // 设置为用户线程，不是守护线程
                    thread.setDaemon(false);
                    thread.start();
                }
            }.run();

        }
        return JSONResult.success();
    }
}
