package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;

    /**
     * 进入资源管理页面
     * @return 视图
     */
    @RequestMapping("index")
    public String index() {
        return "module/module";
    }

    /**
     * 进入授权页面
     * @param roleId 角色id
     * @param request 请求域
     * @return 视图
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, HttpServletRequest request) {
        // 将需要授权的角色id设置到请求域中
        request.setAttribute("roleId", roleId);
        return "role/grant";
    }

    /**
     * 打开添加资源的页面
     * @param grade 层级
     * @param parentId 父菜单ID
     * @param request 请求域
     * @return 视图
     */
    @RequestMapping("toAddModulePage")
    public String toAddModulePage(Integer grade, Integer parentId, HttpServletRequest request) {
        // 将数据设置到请求域中
        request.setAttribute("grade", grade);
        request.setAttribute("parentId", parentId);

        return "module/add";
    }

    /**
     *
     * @param id 菜单管理记录的id
     * @param model Module实体类
     * @return 视图
     */
    @RequestMapping("toUpdateModulePage")
    public String toUpdateModulePage(Integer id, Model model) {
        // 将要修改的资源对象设置到请求域中
        model.addAttribute("module", moduleService.selectByPrimaryKey(id));
        return "module/update";
    }

    /**
     * 查询所有的资源列表
     * @return list
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer roleId) {
        return moduleService.queryAllModules(roleId);
    }

    /**
     * 查询所有的资源列表
     * @return map--> json数据
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryModuleList() {
        return moduleService.queryModuleList();
    }

    /**
     * 添加资源
     * @param module Module实体类
     * @return ResultInfo对象
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addModule(Module module) {
        moduleService.addModule(module);
        return success("添加资源成功！");
    }

    /**
     * 修改资源
     * @param module Module实体类
     * @return ResultInfo对象
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module) {

        moduleService.updateModule(module);
        return success("修改资源成功！");
    }

    /**
     * 删除资源
     * @param id 菜单管理记录的id
     * @return ResultInfo
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id) {

        moduleService.deleteModule(id);
        return success("删除资源成功！");
    }

}
