package com.honghukeji.hhkj.controllers.admin.setting.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UploadLog {
    private int id;
    private int type;
    private int file_type;
    private String file_name;
    private String url;
    private Date atime;
}
