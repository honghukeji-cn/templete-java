package com.honghukeji.hhkj.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer type;//1 文本  2图片  3图文 4数字
    private String title;
    private String value;//配置值
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private int canDel;//是否允许删除
}
