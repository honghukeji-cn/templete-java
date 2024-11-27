package com.honghukeji.hhkj.controllers.admin.backup;

import com.honghukeji.hhkj.annotation.CheckToken;
import com.honghukeji.hhkj.controllers.BaseController;
import com.honghukeji.hhkj.controllers.admin.backup.dto.DbBackUpListDto;
import com.honghukeji.hhkj.controllers.admin.backup.vo.DbBackupVo;
import com.honghukeji.hhkj.entity.Admin;
import com.honghukeji.hhkj.entity.DbBackup;
import com.honghukeji.hhkj.exception.ErrorException;
import com.honghukeji.hhkj.objs.EmptyVo;
import com.honghukeji.hhkj.objs.JSONResult;
import com.honghukeji.hhkj.objs.LocalAdmin;
import com.honghukeji.hhkj.objs.PageVo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RequestMapping("/admin/backup")
@RestController
@CheckToken
public class DbBackUpController extends BaseController {
    //备份
    @RequestMapping("/backUpDb")
    public JSONResult<EmptyVo> backUpDb()
    {
        Admin admin= LocalAdmin.get();
        dbBackupService.backup(admin.getAdmin_id());
        return JSONResult.success();
    }
    //恢复
    @RequestMapping("/restoreDb/{id}")
    public JSONResult<EmptyVo> restoreDb(@PathVariable Integer id){
        dbBackupService.restoreDbById(id);
        return JSONResult.success();
    }
    //文件列表
    @RequestMapping("/getList")
    public JSONResult<PageVo<DbBackupVo>> getList(@RequestBody DbBackUpListDto dto)
    {
        PageVo<DbBackupVo> pageVo=dbBackupService.getList(dto);
        return JSONResult.success(pageVo);
    }
    //下载
    @PostMapping("/download/{id}")
    public InputStreamSource download(@PathVariable Integer id, HttpServletResponse response) throws FileNotFoundException {
        DbBackup backup= dbBackupService.getById(id);
        if(backup==null)
        {
            throw new ErrorException("文件不存在!");
        }
        File file=new File(backup.getFile_path());
        if(!file.exists())
        {
            throw new ErrorException("文件不存在!");
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        response.setHeader("Content-Disposition","attachment; filename=" + backup.getFile_name());
        response.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return resource;
    }
    //删除
    @RequestMapping("/removeBackUpFile/{id}")
    public JSONResult<EmptyVo> removeBackUpFile(@PathVariable Integer id)
    {
        DbBackup backup= dbBackupService.getById(id);
        if(backup==null)
        {
          return JSONResult.error("数据不存在!");
        }
        dbBackupService.removeById(id);
        File file=new File(backup.getFile_path());
        file.delete();
        return JSONResult.success();
    }
}
