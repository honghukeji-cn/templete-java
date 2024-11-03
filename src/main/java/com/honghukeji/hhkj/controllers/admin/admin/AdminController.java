package com.honghukeji.hhkj.controllers.admin.admin;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.honghukeji.hhkj.annotation.CheckToken;
import com.honghukeji.hhkj.controllers.BaseController;
import com.honghukeji.hhkj.controllers.admin.admin.dto.*;
import com.honghukeji.hhkj.controllers.admin.admin.vo.*;
import com.honghukeji.hhkj.entity.Admin;
import com.honghukeji.hhkj.entity.AuthConfig;
import com.honghukeji.hhkj.entity.Role;
import com.honghukeji.hhkj.entity.Setting;
import com.honghukeji.hhkj.helper.Helper;
import com.honghukeji.hhkj.objs.*;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/admin/admin")
@CheckToken
public class AdminController extends BaseController {
    /**
     * 获取登录页面基本信息
     * @return
     */
    @RequestMapping("/getLoginInfo")
    @CheckToken(checkAuth = false)
    public JSONResult<GetLoginInfoVo> getLoginInfo(){
        Admin admin= LocalAdmin.get();
        Role role=roleService.getById(admin.getRole_id());
        JSONArray jarr= (JSONArray) JSONArray.parse(role.getIds());
        System.out.println(jarr);
        String ids= Helper.implodeJsonArray(",",jarr);
        System.out.println(ids);
        //获取允许操作的菜单
        List<AuthMenuVo> menuVos= menuService.getBaseMapper().getAuthMenuList(0,ids);
        int i=0;
        for(AuthMenuVo vo:menuVos){
            List<AuthMenuVo>menuVos1= menuService.getBaseMapper().getAuthMenuList(vo.getId(),ids);
            vo.setChild(menuVos1);
            menuVos.set(i,vo);
            i++;
        }
        Setting setting= settingService.getById(1);//系统名称

        GetLoginInfoVo vo=new GetLoginInfoVo();
        vo.setAvatar(admin.getAvatar()==null?"":admin.getAvatar());
        vo.setMenus(menuVos);
        vo.setName(setting.getValue());
        vo.setUsername(admin.getUsername());

        //查询密码修改
        vo.setChange_pwd_type(0);
        AuthConfig authConfig= authConfigService.getById(1);
        //判断多少天没修改过密码了
        Integer day;
        if(admin.getLast_change_pwd_time()==null)
        {
           day=99999;
        }else{
            day= Helper.dateDifference(admin.getLast_change_pwd_time(),Helper.getDaDate());
        }

        if(day>=authConfig.getPass_wran() && day<authConfig.getPass_max())
        {
            //达到警告但是没有强制
            vo.setChange_pwd_type(1);
            vo.setChange_pwd_tip("您的密码已有"+day+"天未修改了，请注意密码安全!");
        }else if(day>=authConfig.getPass_max())
        {
            vo.setChange_pwd_type(2);
            vo.setChange_pwd_tip("您的密码已有"+day+"天未修改了,请立即修改密码!");
        }
        return  JSONResult.success(vo);
    }
    @RequestMapping("/getPwdRule")
    @CheckToken(checkAuth = false)
    public JSONResult<AuthConfig> getPwdRule()
    {
        AuthConfig authConfig=authConfigService.getById(1);
        return JSONResult.success(authConfig);
    }
    //修改密码
    @RequestMapping("/editPwd")
    @CheckToken(checkAuth = false)
    public JSONResult<EmptyVo> editPwd(@RequestBody EditPwdDto req){
        Admin admin= LocalAdmin.get();
        //校验密码是否正确
        if(!Helper.hashpasswordVerify(req.getOldPwd(),admin.getPassword())){
            return JSONResult.error("密码错误!");
        }
        //设置密码
        Admin uadmin=new Admin();
        uadmin.setAdmin_id(admin.getAdmin_id());
        uadmin.setPassword(Helper.createHashPassword(req.getPassword()));
        uadmin.setLast_change_pwd_time(Helper.getDaDate());
        if(adminService.updateById(uadmin)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }

    //修改头像和昵称
    @RequestMapping("/editAvatar")
    @CheckToken(checkAuth = false)
    public JSONResult<EmptyVo> editAvatar(@RequestBody EditAvatar req){
        if(req.getAvatar().equals("") && req.getUsername().equals("")){
            return JSONResult.error("用户名和头像不能同时为空");
        }
        Admin admin= LocalAdmin.get();
        Admin update=new Admin();
        update.setAdmin_id(admin.getAdmin_id());
        update.setAvatar(req.getAvatar());
        update.setUsername(req.getUsername());
        if(adminService.updateById(update)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //获取查询的角色列表
    @RequestMapping("/getSearchRoleList")
    @CheckToken(checkAuth = false)
    public JSONResult<List<SelectVo>> getSearchRoleList(){
        List<Role>roles=roleService.list();
        List<SelectVo>vos=new LinkedList<>();
        for(Role role:roles){
            SelectVo vo=new SelectVo();
            vo.setValue(role.getRole_id());
            vo.setLabel(role.getRole_name());
            vos.add(vo);
        }
        return JSONResult.success(vos);
    }
    //添加管理员
    @RequestMapping("/addAdmin")
    public JSONResult<EmptyVo> addAdmin(@RequestBody @Valid AddAdminDto req, BindingResult result){
        Helper.checkError(result);
        req.setPassword(Helper.createHashPassword(req.getPassword()));
        Admin admin=new Admin();
        BeanUtils.copyProperties(req,admin);
        if(adminService.save(admin)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //修改管理员
    @RequestMapping("/editAdmin")
    public JSONResult<EmptyVo> editAdmin(@RequestBody @Valid EditAdminDto req, BindingResult result){
        Helper.checkError(result);
        Admin uadmin=new Admin();
        uadmin.setAdmin_id(req.getAdmin_id());
        if(req.getPassword()!=null && !req.getPassword().equals("")){
            uadmin.setPassword(Helper.createHashPassword(req.getPassword()));
            uadmin.setLast_change_pwd_time(Helper.getDaDate());
        }
        uadmin.setUsername(req.getUsername());
        uadmin.setRole_id(req.getRole_id());
        if(adminService.updateById(uadmin)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //删除管理员
    @RequestMapping("/delAdmin")
    public JSONResult<EmptyVo> delAdmin(@RequestBody @Valid DelAdminDto req, BindingResult result){
        Helper.checkError(result);
        if(adminService.removeById(req.getAdmin_id())){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //管理员列表
    @RequestMapping("/adminList")
    public JSONResult<PageVo<AdminListVo>> adminList(@RequestBody AdminListDto req){
        //orderBy a.admin_id asc/desc a.atime asc/desc
        if(!req.getOrderBy().equals("")){
            req.setOrderBy("admin_id "+req.getOrderBy());
        }
        PageHelper.startPage(req.getPage(),req.getSize(),req.getOrderBy());
        List<AdminListVo>vos=adminService.getBaseMapper().getAdminList(req);
        PageInfo<AdminListVo> pageInfo=new PageInfo<>(vos);
        PageVo<AdminListVo> pageVo=new PageVo((int) pageInfo.getTotal(),vos);
        return JSONResult.success(pageVo);
    }
    //冻结解冻管理员
    @RequestMapping("/changeAdminStatus/{admin_id}")
    public JSONResult<EmptyVo> changeAdminStatus(@PathVariable Integer admin_id){
        Admin admin=adminService.getById(admin_id);
        admin.setStatus(admin.getStatus().equals(0)?1:0);
        adminService.updateById(admin);
        return JSONResult.success();
    }
    //管理员操作日志
    @RequestMapping("/adminLog")
    public JSONResult<PageVo<AdminLogVo>> adminLog(@RequestBody AdminLogDto req){
        if(!req.getOrderBy().equals("")){
            req.setOrderBy("id "+req.getOrderBy());
        }
        PageHelper.startPage(req.getPage(),req.getSize(),"atime desc");
        List<AdminLogVo>vos=adminService.getBaseMapper().getAdminLog(req);
        PageInfo<AdminLogVo> pageInfo=new PageInfo<>(vos);
        PageVo<AdminLogVo> pageVo=new PageVo((int) pageInfo.getTotal(),vos);
        return JSONResult.success(pageVo);
    }
    //管理员列表
    @RequestMapping("/getSearchAdminList")
    @CheckToken(routes = {"/admin/admin/adminLog"})
    public JSONResult<List<SelectVo>> getSearchAdminList(){
        List<SelectVo>vos=adminService.getBaseMapper().getSearchAdminList();
        return JSONResult.success(vos);
    }
}
