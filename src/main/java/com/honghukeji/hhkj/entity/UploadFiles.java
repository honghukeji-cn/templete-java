package com.honghukeji.hhkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFiles {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer domain;//文件保存在哪里的 0虚拟文件夹 1七牛 2阿里oss 3腾讯cos 4本地
    private Integer type;//文件类型 0文件夹 1图片 2视频 3 Excel 4 word 5 pdf 6 zip 7 未知类型文件
    private String name;//文件名称
    @TableField("`key`")
    private String key;//上传到第三方的key
    private String url;//文件地址
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date atime;
    private Integer pid;//上级目录ID 顶级目录为0
}
