package com.honghukeji.hhkj.controllers.admin.admin.dto;

import com.honghukeji.hhkj.objs.PageDto;
import lombok.Data;

@Data
public class AdminLogDto extends PageDto {
    private String desc;
    private int admin_id;
    private String address;
    private String ip;
}
