package com.honghukeji.hhkj.controllers.admin.menu;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.honghukeji.hhkj.annotation.CheckToken;
import com.honghukeji.hhkj.controllers.BaseController;
import com.honghukeji.hhkj.controllers.admin.menu.dto.*;
import com.honghukeji.hhkj.controllers.admin.menu.vo.GetMenuListVo;
import com.honghukeji.hhkj.controllers.admin.menu.vo.GetMenusByPidVo;
import com.honghukeji.hhkj.entity.Menu;
import com.honghukeji.hhkj.objs.EmptyVo;
import com.honghukeji.hhkj.objs.JSONResult;
import com.honghukeji.hhkj.objs.PageDto;
import com.honghukeji.hhkj.objs.PageVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/menu")
@CheckToken
public class MenuController extends BaseController {
    //添加菜单
    @RequestMapping("/addMenu")
    public JSONResult<EmptyVo> addMenu(@RequestBody AddMenuDto req){
        Menu menu=new Menu(
                0,req.getPid(),req.getName(),req.getPath(),req.getRoute(),req.getLevel(),req.getIcon(),
                req.getSort(),req.getDisplay(),req.getNeedLog()
        );
        //插入菜单记录
        if(menuService.save(menu)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
    //根据PID获取菜单列表
    @RequestMapping("/getMenusByPid")
    @CheckToken(routes = {"/admin/menu/addMenu","/admin/menu/editMenu"})
    public JSONResult<List<GetMenusByPidVo>> getMenusByPid(@RequestBody GetMenusByPidDto req){
        List<GetMenusByPidVo> vos=menuService.getBaseMapper().getMenusByPid(req.getPid());
        return JSONResult.success(vos);
    }
    //编辑菜单
    @RequestMapping("/editMenu")
    public JSONResult<EmptyVo> editMenu(@RequestBody EditMenuDto req){
        Menu menu=menuService.getBaseMapper().selectById(req.getId());
        if(menu==null){
            return JSONResult.error("菜单不存在!");
        }
        //判断菜单等级
        if(req.getLevel()>1){
            List<GetMenusByPidVo> vos=menuService.getBaseMapper().getMenusByPid(req.getId());
            if(vos.size()>0){//有一层了
                if(req.getLevel()==3){
                    return JSONResult.error("菜单下层级超过三层!");
                }
                if(req.getLevel()==2){
                    for(GetMenusByPidVo vo:vos){
                        List<GetMenusByPidVo> vos1=menuService.getBaseMapper().getMenusByPid(vo.getId());
                        if(vos1.size()>0){
                            //有两层了
                            return JSONResult.error("菜单下层级超过三层!");
                        }
                    }
                }
            }
        }
        Menu menu1=new Menu(
                req.getId(),req.getPid(),req.getName(),req.getPath(),req.getRoute(),req.getLevel(),req.getIcon(),
                req.getSort(),req.getDisplay(),req.getNeedLog()
        );
        //更新菜单
        menuService.updateById(menu1);
        return JSONResult.success();
    }
    //删除菜单
    @RequestMapping("/delMenu")
    public JSONResult<EmptyVo> delMenu(@RequestBody DelMenuDto req){
        //判断是否存在子菜单
        List<GetMenusByPidVo> vos=menuService.getBaseMapper().getMenusByPid(req.getId());
        if(vos.size()>0){
            return JSONResult.error("菜单下存在子菜单无法删除!");
        }
        menuService.removeById(req.getId());
        return JSONResult.success();
    }
    //菜单列表
    @RequestMapping("/menuList")
    public JSONResult<PageVo<GetMenuListVo>> menuList(@RequestBody PageDto req){
        PageHelper.startPage(req.getPage(),req.getSize(),"sort asc");
        List<GetMenuListVo>vos=menuService.getBaseMapper().getMenus(0);
        PageInfo<GetMenuListVo> pageInfo=new PageInfo<>(vos);
        vos=findSons(vos);
        PageVo<GetMenuListVo> pageVo=new PageVo();
        pageVo.setAll((int) pageInfo.getTotal());
        pageVo.setDatas(vos);
        return JSONResult.success(pageVo);
    }
    private List<GetMenuListVo> findSons( List<GetMenuListVo> vos){
        int i=0;
        for(GetMenuListVo vo:vos){
            //查询子菜单
            List<GetMenuListVo>vos1=menuService.getBaseMapper().getMenus(vo.getId());
            vos1=findSons(vos1);
            vo.setChild(vos1);
            vos.set(i,vo);
            i++;
        }
        return vos;
    }
    //设置菜单是否加入日志
    @RequestMapping("/setNeedLog")
    public JSONResult<EmptyVo> setNeedLog(@RequestBody SetNeedLogDto req){
        Menu menu=new Menu();
        menu.setId(req.getId());
        menu.setNeedLog(req.getNeedLog());
        if(menuService.updateById(menu)){
            return JSONResult.success();
        }
        return JSONResult.error();
    }
}
