package com.honghukeji.hhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.honghukeji.hhkj.controllers.admin.admin.vo.AdminListVo;
import com.honghukeji.hhkj.controllers.admin.backup.dto.DbBackUpListDto;
import com.honghukeji.hhkj.controllers.admin.backup.vo.DbBackupVo;
import com.honghukeji.hhkj.dao.DbBackupDao;
import com.honghukeji.hhkj.entity.DbBackup;
import com.honghukeji.hhkj.exception.ErrorException;
import com.honghukeji.hhkj.helper.Helper;
import com.honghukeji.hhkj.objs.PageVo;
import com.honghukeji.hhkj.service.DbBackupService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * 数据库备份记录(DbBackup)表服务实现类
 *
 * @author makejava
 * @since 2024-11-02 14:20:48
 */
@Service
public class DbBackupServiceImpl extends ServiceImpl<DbBackupDao, DbBackup> implements DbBackupService {
    @Value("${spring.datasource.dbname}")
    private String dbname;//数据库名称
    @Value("${spring.datasource.username}")
    private  String username;//数据库用户名
    @Value("${spring.datasource.password}")
    private  String password;//数据库密码
    @Value("${spring.datasource.backuppath}")
    private  String backupPath;//备份文件目录
    @Value("${spring.datasource.host}")
    private  String dbHost;//数据库地址
    @Value("${spring.datasource.port}")
    private  String dbPort;//数据库端口
    @Override
    public void backup(Integer admin_id) {
        String file_name=dbname+ Helper.getStrDate("yyyyMMddHHmmss")+".sql";
        String real_path=backupPath+file_name;
        String command = "mysqldump -h "+dbHost+" -P "+dbPort+" -u " + username + " -p" + password + " " + dbname + " -r " +real_path+" --no-tablespaces";
        System.out.println("command:"+command);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("数据库备份成功！");
                File file=new File(real_path);
                DbBackup dbBackup=new DbBackup();
                dbBackup.setFile_name(file_name);
                dbBackup.setFile_size((int) file.length());
                dbBackup.setFile_path(real_path);
                dbBackup.setAdmin_id(admin_id);
                save(dbBackup);
            } else {
                throw new ErrorException("备份失败:"+process.getErrorStream().toString());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ErrorException("备份失败:"+e.getMessage());
        }

    }

    @SneakyThrows
    @Override
    public void restoreDbById(Integer id) {
        DbBackup backup=getById(id);
        if(backup==null)
        {
            throw new ErrorException("备份信息不存在!");
        }
        File file = new File(backup.getFile_path());
        if(!file.exists())
        {
            throw new ErrorException("备份文件不存在!");
        }
        String command = "mysql -h "+dbHost+" -P "+dbPort+" -u " + username + " -p" + password + " " + dbname;
        System.out.println("command:"+command);
        Process process=null;
        try {
//            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
//            Process process = processBuilder.start();
             process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorException("恢复数据库失败!"+e.getMessage());
        }
        OutputStream outputStream=process.getOutputStream();
        BufferedReader bufferedReader=null;
        try {
            bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(backup.getFile_path()),"utf-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer sb=new StringBuffer();
        String b=null;
        while ((b=bufferedReader.readLine())!=null){
            sb.append(b+"\r\n");
        }
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream,"utf-8");
        outputStreamWriter.write(sb.toString());
        outputStreamWriter.flush();
        outputStreamWriter.close();
        bufferedReader.close();
        outputStream.close();
    }

    @Override
    public PageVo<DbBackupVo> getList(DbBackUpListDto dto) {
        PageHelper.startPage(dto.getPage(),dto.getSize(),"a.id desc");
        List<DbBackupVo> vos=baseMapper.getList(dto);
        PageInfo<DbBackupVo> pageInfo=new PageInfo<>(vos);
        PageVo<DbBackupVo> pageVo=new PageVo((int) pageInfo.getTotal(),vos);
        return pageVo;
    }


}

