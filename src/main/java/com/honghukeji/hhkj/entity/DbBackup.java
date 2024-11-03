package com.honghukeji.hhkj.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据库备份记录(DbBackup)实体类
 *
 * @author yzj
 * @date 2024-11-02 14:20:48
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbBackup {
    @TableId(type= IdType.AUTO)
    private Integer id;
    //备份文件名称
    private String file_name;
    //备份人
    private Integer admin_id;
    //备份文件路径
    private String file_path;
    //文件大小
    private Integer file_size;
    //备份时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date atime;


}

