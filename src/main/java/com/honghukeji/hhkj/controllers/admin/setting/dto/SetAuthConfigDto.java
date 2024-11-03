package com.honghukeji.hhkj.controllers.admin.setting.dto;

import lombok.Data;

@Data
public class SetAuthConfigDto  {
    //密码正则
    private String pwd_reg;
    //密码正则描述
    private String pwd_reg_desc;

    //连续密码错误次数后冻结账号
    private Integer fail_num;
    //清除密码错误记录的时间(秒)
    private Integer fail_num_time;

    //静默多久后登录失效(秒)
    private Integer timeout;
    //密码多少天后必须强制更换
    private Integer pass_max;
    //是否开启自动备份 0不开启 1开启
    private Integer auto_backup;
    //备份数据得cron表达式
    private String bakup_db_cron;
    //多少天开始提示用户修改密码
    private Integer pass_wran;
}
