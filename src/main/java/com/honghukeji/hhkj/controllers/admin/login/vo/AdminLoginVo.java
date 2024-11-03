package com.honghukeji.hhkj.controllers.admin.login.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminLoginVo {
    private String avatar;
    private String nickname;
    private String token;
}
