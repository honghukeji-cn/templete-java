package com.honghukeji.hhkj.controllers;

import com.honghukeji.hhkj.objs.EmptyVo;
import com.honghukeji.hhkj.objs.JSONResult;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrController implements ErrorController {
    @RequestMapping
    public JSONResult<EmptyVo> error(HttpServletRequest request, HttpServletResponse response){
        response.setStatus(404);
        return JSONResult.error("请求地址不存在");
    }
}
