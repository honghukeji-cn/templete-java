package com.honghukeji.hhkj.controllers.admin.admin.dto;

import com.honghukeji.hhkj.objs.PageDto;
import lombok.Data;

@Data
public class AdminListDto extends PageDto {
    private String username;
    private int role_id;
}
