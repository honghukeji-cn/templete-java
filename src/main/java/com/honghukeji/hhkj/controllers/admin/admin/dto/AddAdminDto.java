package com.honghukeji.hhkj.controllers.admin.admin.dto;

import com.honghukeji.hhkj.helper.Helper;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

@Data
public class AddAdminDto {
    @Length(min = 4,max = 12,message = "账号格式错误,为4ß-12位")
    private String username;
    @Length(min = 6,max = 16,message = "密码格式错误,为6-16位")
    private String password;
    @Min(value = 0,message = "角色ID错误")
    private int role_id;
    private String atime= Helper.getStrDate();
}
