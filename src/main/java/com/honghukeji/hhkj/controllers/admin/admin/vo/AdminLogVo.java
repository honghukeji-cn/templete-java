package com.honghukeji.hhkj.controllers.admin.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AdminLogVo {
    private String username;
    private String desc;
    private String ip;
    private String address;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date atime;
}
