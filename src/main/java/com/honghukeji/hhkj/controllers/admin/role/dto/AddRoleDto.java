package com.honghukeji.hhkj.controllers.admin.role.dto;

import com.honghukeji.hhkj.helper.Helper;
import lombok.Data;

@Data
public class AddRoleDto {
    private String role_name;
    private String describe;
    private String ids;
    private String atime= Helper.getStrDate();
}
