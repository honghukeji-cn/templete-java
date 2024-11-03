package com.honghukeji.hhkj.controllers.admin.setting.vo;

import com.honghukeji.hhkj.objs.AliOssEntity;
import com.honghukeji.hhkj.objs.QiniuEntity;
import com.honghukeji.hhkj.objs.TxCosEntity;
import lombok.Data;

@Data
public class GetUploadConfigVo {
    private int visible;
    private QiniuEntity qiniu;
    private AliOssEntity alioss;
    private TxCosEntity txcos;
}
