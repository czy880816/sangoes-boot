/*
 * @Author: jerrychir @sangoes
 * @Date: 2018-11-09 15:41:43
 * @Last Modified by: jerrychir @sangoes
 * @Last Modified time: 2018-11-10 13:27:48
 */
package com.sangoes.boot.uc.modules.admin.controller;

import com.sangoes.boot.common.controller.BaseController;
import com.sangoes.boot.common.msg.Result;
import com.sangoes.boot.common.utils.AuthUtils;
import com.sangoes.boot.uc.modules.admin.dto.MenuDto;
import com.sangoes.boot.uc.modules.admin.dto.MenuDto.AddMenuGroup;
import com.sangoes.boot.uc.modules.admin.entity.SysMenu;
import com.sangoes.boot.uc.modules.admin.service.ISysMenuService;
import com.sangoes.boot.uc.modules.admin.vo.MenuTree;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author jerrychir
 * @since 2018-11-09
 */
@RestController
@RequestMapping("admin/menu")
@Api("菜单管理类")
public class SysMenuController extends BaseController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 添加菜单
     *
     * @param menuDto
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加菜单", notes = "返回添加结果")
    @ResponseBody
    public Result<String> addRole(@RequestBody @Validated(AddMenuGroup.class) MenuDto menuDto) {
        return menuService.addMenu(menuDto);
    }

    /**
     * 获取菜单树形结果
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperation(value = "获取菜单树形结果", notes = "返回树形结果")
    @ResponseBody
    public Result<List<MenuTree>> getMenuTree() {
        return menuService.getMenuTree();
    }

    /**
     * 获取菜单列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取菜单列表", notes = "返回菜单列表结果")
    @ResponseBody
    public Result<List<SysMenu>> getMenuList() {
        return menuService.getMenuList();
    }


    /**
     * 获取当前用户的菜单树形结果
     *
     * @return
     */
    @GetMapping("/user/tree")
    @ApiOperation(value = "获取当前用户的菜单树形结果", notes = "返回树形结果")
    @ResponseBody
    public Result<List<MenuTree>> getUserMenuTree() {
        // 获取当前角色
        List<String> roles = AuthUtils.getListUserRoles();
        // 获取树形菜单
        List<MenuTree> menuTrees = menuService.getUserMenuTree(roles);
        // 返回
        return Result.success(menuTrees, "成功");
    }

}
