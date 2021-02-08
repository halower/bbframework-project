package com.quickstart.bbframework.modules.ums.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newbie.common.api.CommonPage;
import com.newbie.common.api.JsonResult;
import com.quickstart.bbframework.modules.ums.service.UmsAdminService;
import com.quickstart.bbframework.modules.ums.service.UmsRoleService;
import com.quickstart.bbframework.modules.ums.dto.UmsAdminLoginParam;
import com.quickstart.bbframework.modules.ums.dto.UmsAdminParam;
import com.quickstart.bbframework.modules.ums.dto.UpdateAdminPasswordParam;
import com.quickstart.bbframework.modules.ums.model.UmsAdmin;
import com.quickstart.bbframework.modules.ums.model.UmsRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台用户管理
 */
@Controller
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsRoleService roleService;

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            return JsonResult.failed();
        }
        return JsonResult.success(umsAdmin);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            return JsonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return JsonResult.success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        if (refreshToken == null) {
            return JsonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return JsonResult.success(tokenMap);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getAdminInfo(Principal principal) {
        if(principal==null){
            return JsonResult.unauthorized(null);
        }
        String username = principal.getName();
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));
        data.put("icon", umsAdmin.getIcon());
        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId());
        if(CollUtil.isNotEmpty(roleList)){
            List<String> roles = roleList.stream().map(UmsRole::getName).collect(Collectors.toList());
            data.put("roles",roles);
        }
        return JsonResult.success(data);
    }

    @ApiOperation(value = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult logout() {
        return JsonResult.success(null);
    }

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                 @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<UmsAdmin> adminList = adminService.list(keyword, pageSize, pageNum);
        return JsonResult.success(CommonPage.restPage(adminList));
    }

    @ApiOperation("获取指定用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult<UmsAdmin> getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getById(id);
        return JsonResult.success(admin);
    }

    @ApiOperation("修改指定用户信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult update(@PathVariable Long id, @RequestBody UmsAdmin admin) {
        boolean success = adminService.update(id, admin);
        if (success) {
            return JsonResult.success(null);
        }
        return JsonResult.failed();
    }

    @ApiOperation("修改指定用户密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult updatePassword(@Validated @RequestBody UpdateAdminPasswordParam updatePasswordParam) {
        int status = adminService.updatePassword(updatePasswordParam);
        if (status > 0) {
            return JsonResult.success(status);
        } else if (status == -1) {
            return JsonResult.failed("提交参数不合法");
        } else if (status == -2) {
            return JsonResult.failed("找不到该用户");
        } else if (status == -3) {
            return JsonResult.failed("旧密码错误");
        } else {
            return JsonResult.failed();
        }
    }

    @ApiOperation("删除指定用户信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult delete(@PathVariable Long id) {
        boolean success = adminService.delete(id);
        if (success) {
            return JsonResult.success(null);
        }
        return JsonResult.failed();
    }

    @ApiOperation("修改帐号状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult updateStatus(@PathVariable Long id, @RequestParam(value = "status") Integer status) {
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setStatus(status);
        boolean success = adminService.update(id,umsAdmin);
        if (success) {
            return JsonResult.success(null);
        }
        return JsonResult.failed();
    }

    @ApiOperation("给用户分配角色")
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult updateRole(@RequestParam("adminId") Long adminId,
                                 @RequestParam("roleIds") List<Long> roleIds) {
        int count = adminService.updateRole(adminId, roleIds);
        if (count >= 0) {
            return JsonResult.success(count);
        }
        return JsonResult.failed();
    }

    @ApiOperation("获取指定用户的角色")
    @RequestMapping(value = "/role/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId) {
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return JsonResult.success(roleList);
    }
}
