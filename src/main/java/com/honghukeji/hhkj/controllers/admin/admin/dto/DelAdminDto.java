package com.honghukeji.hhkj.controllers.admin.admin.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class DelAdminDto {
    @Min(value = 0,message = "管理员ID错误")
    private int admin_id;
}
