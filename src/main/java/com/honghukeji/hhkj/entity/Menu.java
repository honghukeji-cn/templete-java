package com.honghukeji.hhkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer pid;
    private String name;
    private String path;//前端路由
    private String route;//后端路由
    private Integer level;
    private String  icon;
    private Integer sort;
    private Integer display;
    private Integer needLog;

}
