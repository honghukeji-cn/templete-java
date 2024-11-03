package com.honghukeji.hhkj.controllers.admin.setting.vo;

import lombok.Data;

@Data
public class GetSettingListVo {
    private int id;
    private String title;
    private String value;
    private int type;
    private int canDel;
}
