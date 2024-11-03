package com.honghukeji.hhkj.controllers.admin.setting.dto;


import com.honghukeji.hhkj.objs.PageDto;
import lombok.Data;

@Data
public class GetFileListDto extends PageDto {
    private String name;
    private int type;
    private int pid;
}
