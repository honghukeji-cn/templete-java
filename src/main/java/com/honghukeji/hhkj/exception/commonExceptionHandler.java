package com.honghukeji.hhkj.exception;


import com.honghukeji.hhkj.helper.Helper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class commonExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Map exceptionHandler(HttpServletRequest request, Exception e) {
//        Map data=new HashMap();
//        data.put("line",e.)
        Map json = new HashMap();
        json.put("msg", e.getMessage());
        json.put("code", Helper.ErrorCode);
        json.put("data",e);
        return json;
    }
    @ExceptionHandler(value = NoAuthException.class)
    @ResponseBody
    public Map noAuthExceptionHandler(NoAuthException e) {
        Map json = new HashMap();
        json.put("msg", e.getMessage());
        json.put("code", Helper.NoAuthCode);
        json.put("data",new String[]{});
        return json;
    }
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public Map notLoginExceptionHandler(NotLoginException e) {
        Map json = new HashMap();
        json.put("msg", e.getMessage());
        json.put("code", Helper.NotLoginCode);
        json.put("data",new String[]{});
        return json;
    }

}
