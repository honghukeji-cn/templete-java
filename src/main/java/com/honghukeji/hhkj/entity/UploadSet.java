package com.honghukeji.hhkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class UploadSet {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer visible;
    private String qiniu;
    private String alioss;
    private String txcos;
}
