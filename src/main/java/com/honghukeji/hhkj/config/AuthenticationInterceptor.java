package com.honghukeji.hhkj.config;

import com.alibaba.fastjson.JSONArray;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.honghukeji.hhkj.annotation.CheckToken;
import com.honghukeji.hhkj.entity.*;
import com.honghukeji.hhkj.service.AsyncService;
import com.honghukeji.hhkj.exception.NoAuthException;
import com.honghukeji.hhkj.exception.NotLoginException;
import com.honghukeji.hhkj.helper.Helper;
import com.honghukeji.hhkj.helper.Redis;
import com.honghukeji.hhkj.objs.LocalAdmin;
import com.honghukeji.hhkj.service.AuthConfigService;
import com.honghukeji.hhkj.service.impl.AdminLogServiceImpl;
import com.honghukeji.hhkj.service.impl.AdminServiceImpl;
import com.honghukeji.hhkj.service.impl.MenuServiceImpl;
import com.honghukeji.hhkj.service.impl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    AdminServiceImpl adminService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    MenuServiceImpl menuService;
    @Autowired
    Redis redis;
    @Autowired
    AdminLogServiceImpl adminLogService;
    @Autowired
    AsyncService asyncService;
    @Autowired
    AuthConfigService authConfigService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        if(request.getRequestURI().equals("upload"))
        {
            //校验上传
            String uptoken=request.getHeader("token");// 从 http 请求头中取出 token
            if(!redis.hasKey(uptoken)){
                throw new RuntimeException("token校验失败!");
            }
            return true;
        }
        //校验权限
        HandlerMethod handlerMethod=(HandlerMethod) handler;
        Method method=handlerMethod.getMethod();
        CheckToken checkToken =handlerMethod.getBean().getClass().getDeclaredAnnotation(CheckToken.class);
        if(checkToken !=null && checkToken.required())
        {//需要验证权限
            String token=request.getHeader("token");// 从 http 请求头中取出 token
            // 执行认证
            if (token == null || token=="false") {
                throw new NotLoginException();
            }
            // 获取 token 中的 user id
            int userId=0;
            try {
                userId = Integer.parseInt(JWT.decode(token).getAudience().get(0));
            } catch (JWTDecodeException j) {
                throw new NotLoginException();
            }catch (NumberFormatException n){
                throw new NotLoginException();
            }
            Admin user = adminService.getById(userId);
            if (user == null) {
                throw new NotLoginException();
            }
            // 验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            try {
                jwtVerifier.verify(token);
            } catch (JWTVerificationException e) {
                throw new NotLoginException();
            }
            String redisToken=redis.get("adminToken_"+user.getAdmin_id());
            if(redisToken==null || !redisToken.equals(token)){
                throw new NotLoginException();
            }
            AuthConfig authConfig=authConfigService.getById(1);
            //更新token过期时间
            redis.setEx("adminToken_"+user.getAdmin_id(),redisToken,authConfig.getTimeout(), TimeUnit.SECONDS);
            //权限认证
            CheckToken methodToken=method.getAnnotation(CheckToken.class);
            if(methodToken==null || methodToken.checkAuth()){
                //如果需要校验权限
                Role role=roleService.getById(user.getRole_id());
                if(role==null){
                    throw new NoAuthException();
                }

                JSONArray jarr= (JSONArray) JSONArray.parse(role.getIds());
                String[]ids= new String[jarr.size()];
                for(int i=0;i<jarr.size();i++){
                    ids[i]= jarr.getString(i);
                }
                //判断是否关联权限
                String route="";
                String[] uris=request.getRequestURI().split("/");
                for(int i=0;i<4;i++)
                {
                    route+=uris[i];
                    if(i<3)
                    {
                        route+="/";
                    }
                }
                System.out.println("route"+route);
                System.out.println("route"+request.getRequestURI());
                String[] routes=new String[]{route};
                if(methodToken!=null && methodToken.routes().length!=0){
                    routes=methodToken.routes();
                }

                List<Menu>menus=menuService.list(new QueryWrapper<Menu>().in("route",routes).in("id",ids));
                if(menus.size()==0){
                    throw new NoAuthException();
                }
                if(menus.size()==1){
                    Menu menu=menus.get(0);
                    System.out.println(menu);
                    if(menu.getNeedLog()==1){//如果需要记录日志
                        asyncService.addAdminLog(new AdminLog(null,user.getAdmin_id(),
                                Helper.getIp(request),"未知",
                                Helper.getDaDate(),request.getRequestURI(),menu.getName()));
                    }
                }
            }
            LocalAdmin.set(user);
        }
        return  true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
