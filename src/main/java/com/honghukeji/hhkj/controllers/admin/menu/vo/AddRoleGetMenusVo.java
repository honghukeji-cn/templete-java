package com.honghukeji.hhkj.controllers.admin.menu.vo;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class AddRoleGetMenusVo {
    private int id;
    private String name;
    private List<AddRoleGetMenusVo> child=new LinkedList<>();
}
