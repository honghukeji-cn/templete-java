package com.honghukeji.hhkj.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.honghukeji.hhkj.controllers.admin.setting.vo.UploadLog;
import com.honghukeji.hhkj.entity.UploadFiles;
import com.honghukeji.hhkj.entity.UploadSet;
import com.honghukeji.hhkj.exception.ErrorException;
import com.honghukeji.hhkj.helper.AliOSS;
import com.honghukeji.hhkj.helper.Helper;
import com.honghukeji.hhkj.helper.Qiniu;
import com.honghukeji.hhkj.helper.TXCos;
import com.honghukeji.hhkj.objs.AliOssEntity;
import com.honghukeji.hhkj.objs.JSONResult;
import com.honghukeji.hhkj.objs.QiniuEntity;
import com.honghukeji.hhkj.objs.TxCosEntity;
import lombok.SneakyThrows;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class UploadController extends BaseController{
    @Value("${file.upload-path}")
    private String uploadPath;
    @Value("${file.upload-url}")
    private String uploadUrl;
    @RequestMapping("/upload")
    public JSONResult upload(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return JSONResult.error("请上传文件");
        }
        String originalFilename=file.getOriginalFilename();
        String[] arr=originalFilename.split("\\.");
        String extName=arr[arr.length -1].toLowerCase(Locale.ROOT);
        String[] allowFiles=new String[]{"zip","doc","docx","rar","png","jpeg","jpg","xls","xlsx","pdf","mp4","avi","flv"};
        if(!Arrays.asList(allowFiles).contains(extName))
        {
            return JSONResult.error("不支持上传此类文件:"+extName);
        }
        String filename=checkFile(file.getOriginalFilename());
        if(extName.equals("pdf"))
        {
            try {
                PDDocument document = PDDocument.load(file.getInputStream());
                PDFTextStripper pdfStripper = new PDFTextStripper();
                pdfStripper.getText(document);
            } catch (IOException e) {
                throw new ErrorException("PDF文档疑似存在XSS攻击脚本!禁止上传!");
            }

        }
        try {
            byte[] bytes=file.getBytes();
            File filepath=new File(uploadPath);
            if(!filepath.exists()){
                filepath.mkdir();
            }
            Path path= Paths.get(uploadPath+filename);
            Files.write(path,bytes);
        }catch (IOException e){
            e.printStackTrace();
            return  JSONResult.error(e.getMessage());
        }
        Map json=new HashMap();
        json.put("name",filename);
        json.put("url",uploadUrl+filename);
        return JSONResult.success(json);
    }

    private String checkFile(String filename){
        String[] arr=filename.split("\\.");
        String name=filename.replace("."+arr[arr.length -1],"");
        String extName=arr[arr.length -1];
        if(extName.equals("doc"))
        {
            extName="docx";
            filename+="x";
        }
        File oldFile=new File(uploadPath+filename);
        if(oldFile.exists()){
            name=name+"1";
            return checkFile(name+"."+extName);
        }
        return filename;
    }

    private String doUpload(MultipartFile file)
    {
        if(file.isEmpty()){
            throw new ErrorException("请上传文件");
        }
        String filename=checkFile(file.getOriginalFilename());
        try {
            byte[] bytes=file.getBytes();
            File filepath=new File(uploadPath);
            if(!filepath.exists()){
                filepath.mkdir();
            }
            Path path= Paths.get(uploadPath+filename);
            Files.write(path,bytes);
        }catch (IOException e){
            e.printStackTrace();
            throw new ErrorException(e.getMessage());
        }

        return filename;
    }


    @RequestMapping( value = "/ueditor",method = RequestMethod.GET)
    public Object ueditor(@RequestParam(value = "action") String action, HttpServletRequest request){
        if(action.equals("config")){
            JSONObject json=JSONObject.parseObject(configStr);
            return json;
        }
        if(action.equals("listimage")){
            int page=Integer.parseInt(request.getParameter("start"));
            int size=Integer.parseInt(request.getParameter("size"));
            PageHelper.startPage(page,size,"id desc");
            List<UploadLog> lists=uploadFilesService.getBaseMapper().getImgList();
            PageInfo<UploadLog> pageInfo=new PageInfo<>(lists);
            Map json=new HashMap();
            json.put("list",lists);
            json.put("start",page);
            json.put("state","SUCCESS");
            json.put("total",pageInfo.getTotal());
            return json;

        }
        return null;
    }
    @RequestMapping( value = "/ueditor",method = RequestMethod.POST)
    public Object upload_ue(@RequestParam(value = "upfile") MultipartFile multipartFile) throws IOException {

        if(multipartFile.isEmpty()){
            return "没有文件";
        }
        String fileUrl="";
        //查询现在上传方式
        UploadSet config=uploadSetService.getById(1);
        if(config==null){
            return "没有配置上传方式";
        }
        if(config.getVisible()==1){
            //七牛
            QiniuEntity qiniuEntity= JSON.toJavaObject(JSONObject.parseObject(config.getQiniu()),QiniuEntity.class);
            Qiniu qiniu=new Qiniu(qiniuEntity);
            qiniu.uploadFile(multipartFile.getBytes(),multipartFile.getOriginalFilename());
            fileUrl=qiniu.getDomain()+"/"+multipartFile.getOriginalFilename();
        }
        else if(config.getVisible()==2){
            //阿里oss
            AliOssEntity aliOssEntity=JSON.toJavaObject(JSONObject.parseObject(config.getAlioss()),AliOssEntity.class);
            AliOSS aliOSS=new AliOSS(aliOssEntity);
            aliOSS.uploadFile(multipartFile.getBytes(),multipartFile.getOriginalFilename());
            fileUrl=aliOSS.getDomain()+multipartFile.getOriginalFilename();
        }
        else if(config.getVisible()==3){
            //腾讯cos
            TxCosEntity txCosEntity=JSON.toJavaObject(JSONObject.parseObject(config.getTxcos()),TxCosEntity.class);
            TXCos txCos=new TXCos(txCosEntity);
            final File excelFile =File.createTempFile(multipartFile.getOriginalFilename(),null);
            txCos.uploadFile(excelFile,multipartFile.getOriginalFilename());
            fileUrl=txCos.getPath()+multipartFile.getOriginalFilename();
        }else if(config.getVisible()==4){
            //本地上传
            fileUrl="http://192.168.0.87/uploads/"+doUpload(multipartFile);

        }else{
            return JSONResult.error("未配置上传信息");
        }
        //文件类型
        String fileType=multipartFile.getContentType();
        int type;
        if(fileType.contains("image")){
            type=1;
        }else if(fileType.contains("video")){
            type=2;
        }else{
            type=3;
        }
        //插入上传记录

        UploadFiles log=new UploadFiles();
        log.setAtime(Helper.getDaDate());
        log.setName(multipartFile.getOriginalFilename());
        log.setType(type);
        log.setDomain(config.getVisible());
        log.setUrl(fileUrl);
        uploadFilesService.save(log);
        Map json=new HashMap();
        json.put("state","SUCCESS");
        json.put("original",multipartFile.getOriginalFilename());
        json.put("title",multipartFile.getOriginalFilename());
        json.put("url",fileUrl);
        json.put("type","."+fileType);
        return json;
    }
    private String configStr="{\n" +
            "    /* 上传图片配置项 */\n" +
            "    \"imageActionName\": \"uploadimage\", /* 执行上传图片的action名称 */\n" +
            "    \"imageFieldName\": \"upfile\", /* 提交的图片表单名称 */\n" +
            "    \"imageMaxSize\": 2048000, /* 上传大小限制，单位B */\n" +
            "    \"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 上传图片格式显示 */\n" +
            "    \"imageCompressEnable\": true, /* 是否压缩图片,默认是true */\n" +
            "    \"imageCompressBorder\": 1600, /* 图片压缩最长边限制 */\n" +
            "    \"imageInsertAlign\": \"none\", /* 插入的图片浮动方式 */\n" +
            "    \"imageUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
            "    \"imagePathFormat\": \"/ueditor/php/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
            "                                /* {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 */\n" +
            "                                /* {rand:6} 会替换成随机数,后面的数字是随机数的位数 */\n" +
            "                                /* {time} 会替换成时间戳 */\n" +
            "                                /* {yyyy} 会替换成四位年份 */\n" +
            "                                /* {yy} 会替换成两位年份 */\n" +
            "                                /* {mm} 会替换成两位月份 */\n" +
            "                                /* {dd} 会替换成两位日期 */\n" +
            "                                /* {hh} 会替换成两位小时 */\n" +
            "                                /* {ii} 会替换成两位分钟 */\n" +
            "                                /* {ss} 会替换成两位秒 */\n" +
            "                                /* 非法字符 \\ : * ? \" < > | */\n" +
            "                                /* 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename */\n" +
            "\n" +
            "    /* 涂鸦图片上传配置项 */\n" +
            "    \"scrawlActionName\": \"uploadscrawl\", /* 执行上传涂鸦的action名称 */\n" +
            "    \"scrawlFieldName\": \"upfile\", /* 提交的图片表单名称 */\n" +
            "    \"scrawlPathFormat\": \"/ueditor/php/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
            "    \"scrawlMaxSize\": 2048000, /* 上传大小限制，单位B */\n" +
            "    \"scrawlUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
            "    \"scrawlInsertAlign\": \"none\",\n" +
            "\n" +
            "    /* 截图工具上传 */\n" +
            "    \"snapscreenActionName\": \"uploadimage\", /* 执行上传截图的action名称 */\n" +
            "    \"snapscreenPathFormat\": \"/ueditor/php/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
            "    \"snapscreenUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
            "    \"snapscreenInsertAlign\": \"none\", /* 插入的图片浮动方式 */\n" +
            "\n" +
            "    /* 抓取远程图片配置 */\n" +
            "    \"catcherLocalDomain\": [\"127.0.0.1\", \"localhost\", \"img.baidu.com\"],\n" +
            "    \"catcherActionName\": \"catchimage\", /* 执行抓取远程图片的action名称 */\n" +
            "    \"catcherFieldName\": \"source\", /* 提交的图片列表表单名称 */\n" +
            "    \"catcherPathFormat\": \"/ueditor/php/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
            "    \"catcherUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
            "    \"catcherMaxSize\": 2048000, /* 上传大小限制，单位B */\n" +
            "    \"catcherAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 抓取图片格式显示 */\n" +
            "\n" +
            "    /* 上传视频配置 */\n" +
            "    \"videoActionName\": \"uploadvideo\", /* 执行上传视频的action名称 */\n" +
            "    \"videoFieldName\": \"upfile\", /* 提交的视频表单名称 */\n" +
            "    \"videoPathFormat\": \"/ueditor/php/upload/video/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
            "    \"videoUrlPrefix\": \"\", /* 视频访问路径前缀 */\n" +
            "    \"videoMaxSize\": 102400000, /* 上传大小限制，单位B，默认100MB */\n" +
            "    \"videoAllowFiles\": [\n" +
            "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
            "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"], /* 上传视频格式显示 */\n" +
            "\n" +
            "    /* 上传文件配置 */\n" +
            "    \"fileActionName\": \"uploadfile\", /* controller里,执行上传视频的action名称 */\n" +
            "    \"fileFieldName\": \"upfile\", /* 提交的文件表单名称 */\n" +
            "    \"filePathFormat\": \"/ueditor/php/upload/file/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
            "    \"fileUrlPrefix\": \"\", /* 文件访问路径前缀 */\n" +
            "    \"fileMaxSize\": 51200000, /* 上传大小限制，单位B，默认50MB */\n" +
            "    \"fileAllowFiles\": [\n" +
            "        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
            "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
            "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
            "        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
            "        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
            "    ], /* 上传文件格式显示 */\n" +
            "\n" +
            "    /* 列出指定目录下的图片 */\n" +
            "    \"imageManagerActionName\": \"listimage\", /* 执行图片管理的action名称 */\n" +
            "    \"imageManagerListPath\": \"/ueditor/php/upload/image/\", /* 指定要列出图片的目录 */\n" +
            "    \"imageManagerListSize\": 20, /* 每次列出文件数量 */\n" +
            "    \"imageManagerUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
            "    \"imageManagerInsertAlign\": \"none\", /* 插入的图片浮动方式 */\n" +
            "    \"imageManagerAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 列出的文件类型 */\n" +
            "\n" +
            "    /* 列出指定目录下的文件 */\n" +
            "    \"fileManagerActionName\": \"listfile\", /* 执行文件管理的action名称 */\n" +
            "    \"fileManagerListPath\": \"/ueditor/php/upload/file/\", /* 指定要列出文件的目录 */\n" +
            "    \"fileManagerUrlPrefix\": \"\", /* 文件访问路径前缀 */\n" +
            "    \"fileManagerListSize\": 20, /* 每次列出文件数量 */\n" +
            "    \"fileManagerAllowFiles\": [\n" +
            "        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
            "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
            "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
            "        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
            "        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
            "    ] /* 列出的文件类型 */\n" +
            "\n" +
            "}";
}
