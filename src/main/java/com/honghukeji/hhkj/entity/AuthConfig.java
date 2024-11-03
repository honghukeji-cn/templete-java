package com.honghukeji.hhkj.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录安全配置(AuthConfig)实体类
 *
 * @author yzj
 * @date 2024-11-01 16:16:00
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthConfig {
    private Integer id;
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
    //多少天开始提示用户修改密码
    private Integer pass_wran;
    //是否开启自动备份 0不开启 1开启
    private Integer auto_backup;
    //备份数据得cron表达式
    private String bakup_db_cron;
    //上次编辑时间
    private Date atime;


}

