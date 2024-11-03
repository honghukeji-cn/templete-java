package com.honghukeji.hhkj.controllers.admin.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.honghukeji.hhkj.annotation.CheckToken;
import com.honghukeji.hhkj.controllers.BaseController;
import com.honghukeji.hhkj.controllers.admin.menu.vo.AddRoleGetMenusVo;
import com.honghukeji.hhkj.controllers.admin.menu.vo.GetMenusByPidVo;
import com.honghukeji.hhkj.controllers.admin.role.dto.AddRoleDto;
import com.honghukeji.hhkj.controllers.admin.role.dto.DelRoleDto;
import com.honghukeji.hhkj.controllers.admin.role.dto.EditRoleDto;
import com.honghukeji.hhkj.entity.Admin;
import com.honghukeji.hhkj.entity.Role;
import com.honghukeji.hhkj.helper.Helper;
import com.honghukeji.hhkj.objs.EmptyVo;
import com.honghukeji.hhkj.objs.JSONResult;
import com.honghukeji.hhkj.objs.PageDto;
import com.honghukeji.hhkj.objs.PageVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/admin/role")
@CheckToken
public class RoleController extends BaseController {
    //角色列表
    @RequestMapping("/roleList")
    public JSONResult<PageVo<Role>> roleList(@RequestBody PageDto req){
        if(!req.getOrderBy().equals("")){
            req.setOrderBy("role_id "+req.getOrderBy());
        }
        PageHelper.startPage(req.getPage(),req.getSize(),req.getOrderBy());
        List<Role> vos=roleService.getBaseMapper().selectList(null);
        PageInfo<Role> pageInfo=new PageInfo<>(vos);
        PageVo<Role> pageVo=new PageVo();
        pageVo.setAll((int) pageInfo.getTotal());
        pageVo.setDatas(vos);
        return JSONResult.success(pageVo);
    }
    //新增角色获取所有的菜单
    @RequestMapping("/addRoleGetMenus")
    @CheckToken(routes = {"/admin/role/addRole","/admin/role/editRole"})
    public JSONResult<List<AddRoleGetMenusVo>> addRoleGetMenus(){
        List<GetMenusByPidVo>menus= menuService.getBaseMapper().getMenusByPid(0);
        List<AddRoleGetMenusVo>vos=getSons(menus);
        return JSONResult.success(vos);
    }

    private List<AddRoleGetMenusVo> getSons(List<GetMenusByPidVo>menus){
        List<AddRoleGetMenusVo>vos=new LinkedList<>();
        for(GetMenusByPidVo vo:menus){
            AddRoleGetMenusVo v=new AddRoleGetMenusVo();
            v.setId(vo.getId());
            v.setName(vo.getName());
            List<GetMenusByPidVo>menus1= menuService.getBaseMapper().getMenusByPid(vo.getId());
            v.setChild(getSons(menus1));
            vos.add(v);
        }
        return vos;
    }
    //新增角色
    @RequestMapping("/addRole")
    public JSONResult<EmptyVo> addRole(@RequestBody AddRoleDto req){
        Role role=new Role(
                0,req.getRole_name(),req.getIds(),req.getDescribe(), Helper.getDaDate()
        );
        if(roleService.save(role)){
            return  JSONResult.success();
        }
        return JSONResult.error();
    }
    //编辑角色
    @RequestMapping("/editRole")
    public JSONResult<EmptyVo> editRole(@RequestBody EditRoleDto req){
        Role role=new Role(
                req.getRole_id(),req.getRole_name(),req.getIds(),req.getDescribe(), Helper.getDaDate()
        );
        if(roleService.updateById(role)){
            return  JSONResult.success();
        }
        return JSONResult.error();
    }
    //删除角色
    @RequestMapping("/delRole")
    public JSONResult<EmptyVo> delRole(@RequestBody DelRoleDto req){
        List<Admin> admins=adminService.getBaseMapper().selectList(
                new QueryWrapper<Admin>().eq("role_id",req.getRole_id())
        );
        if(admins.size()>0){
            return JSONResult.error("该角色下存在管理员，无法删除!");
        }
        if(roleService.removeById(req.getRole_id())){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
}
