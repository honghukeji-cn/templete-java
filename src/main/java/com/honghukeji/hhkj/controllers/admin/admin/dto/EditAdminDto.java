package com.honghukeji.hhkj.controllers.admin.admin.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

@Data
public class EditAdminDto {
    @Min(value = 0,message = "管理员ID错误")
    private int admin_id;
    @Min(value = 0,message = "角色ID错误")
    private int role_id;

    private String password;
    @Length(min = 4,max = 12,message = "账号格式错误,为4-12位")
    private String username;
}
