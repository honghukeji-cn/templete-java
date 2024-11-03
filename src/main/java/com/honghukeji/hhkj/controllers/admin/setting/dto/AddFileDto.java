package com.honghukeji.hhkj.controllers.admin.setting.dto;

import com.honghukeji.hhkj.helper.Helper;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddFileDto {
    @Min(value = 0,message = "文件域错误")
    @Max(value = 4,message = "文件域错误")
    @NotNull(message = "文件域不能为空")
    private int domain;//文件保存在哪里的 0虚拟文件夹 1七牛 2阿里oss 3腾讯cos 4本地
    @Min(value = 1,message = "文件类型错误")
    @Max(value = 8,message = "文件类型错误")
    @NotNull(message = "文件类型不能为空")
    private int type;//文件类型 8文件夹 1图片 2视频 3 Excel 4 word 5 pdf 6 zip 7 未知类型文件
    @NotNull(message = "文件名称不能为空")
    @NotBlank(message = "文件名称不能为空")
    private String name;//文件名称
    @NotNull(message = "key不能为空")
    private String key;//上传到第三方的key
    @NotNull(message = "url不能为空")
    private String url;//文件地址
    @NotNull(message = "上级目录ID不能为空")
    private int pid;//上级目录ID 顶级目录为0
    private String atime= Helper.getStrDate();
}
