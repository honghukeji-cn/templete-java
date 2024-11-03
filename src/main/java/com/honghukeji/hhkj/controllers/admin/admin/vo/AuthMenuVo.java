package com.honghukeji.hhkj.controllers.admin.admin.vo;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class AuthMenuVo {
    private int id;
    private String title;
    private String icon;
    private String path;
    private List<AuthMenuVo> child=new LinkedList<>();
}
