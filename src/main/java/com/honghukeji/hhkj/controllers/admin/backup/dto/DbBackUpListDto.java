package com.honghukeji.hhkj.controllers.admin.backup.dto;

import com.honghukeji.hhkj.objs.PageDto;
import lombok.Data;

@Data
public class DbBackUpListDto extends PageDto {
    private String stime;
    private String etime;
    private Integer admin_id;
}
