package com.honghukeji.hhkj.config;


import com.honghukeji.hhkj.entity.AuthConfig;
import com.honghukeji.hhkj.service.AuthConfigService;
import com.honghukeji.hhkj.service.DbBackupService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;


@Component      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling  // 2.开启定时任务
@Data
public class ScheduleConfig implements SchedulingConfigurer {
    @Autowired
    AuthConfigService authConfigService;
    private String backupDbCron;
    public ScheduledTaskRegistrar taskRegistrar;
    @Autowired
    DbBackupService dbBackupService;
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        AuthConfig authConfig= authConfigService.getById(1);
        backupDbCron=authConfig.getBakup_db_cron();
        // 动态使用cron表达式设置循环间隔
        if(authConfig.getAuto_backup().equals(1))
        {
            taskRegistrar.addTriggerTask(new Runnable() {
                @Override
                public void run() {
                    dbBackupService.backup(0);
                }
            }, new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    // 使用CronTrigger触发器，可动态修改cron表达式来操作循环规则
                    System.out.println("开启备份定时任务了");
                    CronTrigger cronTrigger = new CronTrigger(backupDbCron);
                    Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
                    return nextExecutionTime;
                }
            });
        }
    }
}

