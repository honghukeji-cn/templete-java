package com.honghukeji.hhkj.controllers.admin.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AdminListVo{
    private int admin_id;
    private int role_id;
    private String username;
    private String last_login_time;
    private String last_login_ip;
    private String role_name;
    private Integer status;//是否冻结
    private String  last_try_login_ip;//上次登录失败的IP
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date last_try_login_time;//上次登录失败的时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date last_change_pwd_time;//上次修改密码的时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String atime;
}
