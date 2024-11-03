package com.honghukeji.hhkj.controllers.admin.login.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class AdminLoginDto {
    @Length(min = 6,max = 12,message = "账号格式错误")
    private String username;
    @Length(min = 6,max = 16,message = "密码格式错误")
    private String password;
    @Length(min = 4,max = 4,message = "图形验证码错误")
    private String code;
    @NonNull
    @NotBlank
    private String uuid;
}
