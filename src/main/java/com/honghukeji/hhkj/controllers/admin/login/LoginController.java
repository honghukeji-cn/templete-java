package com.honghukeji.hhkj.controllers.admin.login;

import com.honghukeji.hhkj.HhkjPlusApplication;
import com.honghukeji.hhkj.config.ScheduleConfig;
import com.honghukeji.hhkj.controllers.BaseController;
import com.honghukeji.hhkj.controllers.admin.login.dto.AdminLoginDto;
import com.honghukeji.hhkj.controllers.admin.login.vo.AdminLoginVo;
import com.honghukeji.hhkj.controllers.admin.login.vo.GetSystemNameVo;
import com.honghukeji.hhkj.controllers.admin.login.vo.GetVerifyVo;
import com.honghukeji.hhkj.entity.*;
import com.honghukeji.hhkj.exception.ErrorException;
import com.honghukeji.hhkj.helper.Helper;
import com.honghukeji.hhkj.helper.ValidateCode;
import com.honghukeji.hhkj.objs.EmptyVo;
import com.honghukeji.hhkj.objs.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin/login")
public class LoginController extends BaseController {
    //获取系统名称
    @RequestMapping("/getSystemName")
    public JSONResult<GetSystemNameVo> getSystemName(){
        Setting setting=settingService.getById(1);
        GetSystemNameVo vo=new GetSystemNameVo();
        vo.setName(setting.getValue());
        return JSONResult.success(vo);
    }
    //获取图形验证码
    @RequestMapping("/getCaptcha")
    @CrossOrigin
    public JSONResult<GetVerifyVo> getVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ValidateCode vCode = new ValidateCode(151,60,4,200);
        byte[] buff = imageToBytes(vCode.getBuffImg(), "png");
        String uuid = UUID.randomUUID().toString();
        redis.set("verify_code_"+uuid,vCode.getCode());
        String encode = Base64.getEncoder().encodeToString(buff);
        GetVerifyVo vo=new GetVerifyVo();
        vo.setImg("data:image/png;base64,"+encode);
        vo.setUuid(uuid);
        return JSONResult.success(vo);
    }
    private byte[] imageToBytes(BufferedImage buffImg, String png) throws IOException {
        ByteArrayOutputStream outStream =new ByteArrayOutputStream();
        ImageIO.write(buffImg, png, outStream);
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
    @RequestMapping("/login")
    public JSONResult<AdminLoginVo> login(@RequestBody AdminLoginDto req, HttpServletRequest request){
        String rediskey="verify_code_"+req.getUuid();
        String verifyCode=redis.get(rediskey);
        if(verifyCode==null){
            return JSONResult.error("验证码错误!");
        }
        redis.delete(rediskey);
        verifyCode=verifyCode.toLowerCase();
        if(!verifyCode.equals(req.getCode().toLowerCase())){
            return JSONResult.error("验证码错误!");
        }
        //查询用户信息
        Admin user=adminService.getBaseMapper().findByUsername(req.getUsername());
        if(user==null){
            return JSONResult.error("用户不存在");
        }
        if(!user.getStatus().equals(1)){
            return JSONResult.error("ALERT账号已被冻结，请联系管理员解除!");
        }
        AuthConfig authConfig= authConfigService.getById(1);
        //校验密码
        if(!Helper.hashpasswordVerify(req.getPassword(),user.getPassword())){
            //记录用户密码错误

            //查询上次登录错误和本次的相差时间
            long last=0;
            if(user.getLast_try_login_time()!=null)
            {
                last=user.getLast_try_login_time().getTime();
            }
            long now_time=Helper.getDaDate().getTime();
            //如果时间差超过系统设定的值 重新计数
            long cha=(now_time-last)/1000;
            Integer fail_num=user.getFail_num()+1;
            if(cha>authConfig.getFail_num_time())
            {
                fail_num=1;
            }

            user.setLast_try_login_ip(Helper.getIp(request));
            user.setLast_try_login_time(Helper.getDaDate());
            user.setFail_num(fail_num);
            if(fail_num>=authConfig.getFail_num())
            {
                //冻结账号
                user.setStatus(0);
                adminService.updateById(user);
                return JSONResult.error("ALERT密码连续错误"+fail_num+"次,账号已被冻结,请联系管理员解除!");
            }
            adminService.updateById(user);
            if(authConfig.getFail_num()-fail_num==1)
            {
                return JSONResult.error("ALERT密码已连续错误"+fail_num+"次,再次输入错误将冻结账号!");
            }
            return JSONResult.error("密码错误!");
        }
        //创建token
        String token= Helper.getJWTToken(user.getAdmin_id()+"",user.getPassword());
        redis.setEx("adminToken_"+user.getAdmin_id(),token,authConfig.getTimeout(), TimeUnit.SECONDS);
        AdminLoginVo vo=new AdminLoginVo(user.getAvatar(),user.getUsername(),token);

        //插入操作记录-异步
        AdminLog log=new AdminLog(
                null,user.getAdmin_id(),Helper.getIp(request),"未知",Helper.getDaDate(),request.getRequestURI(),"用户登录"
        );
        asyncService.addAdminLog(log);
        //更新上次登录时间和IP
        Admin upAdmin=new Admin();
        upAdmin.setAdmin_id(user.getAdmin_id());
        upAdmin.setLast_login_ip(Helper.getIp(request));
        upAdmin.setLast_login_time(Helper.getDaDate());
        upAdmin.setFail_num(0);
        adminService.updateById(upAdmin);
        return JSONResult.success(vo);
    }

}
