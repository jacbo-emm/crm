package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("cus_dev_plan")
@Controller
public class CusDevPlanController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;

    /**
     * 进入客户开发计划页面
     * @return 视图
     */
    @RequestMapping("index")
    public String index() {
        return "cusDevPlan/cus_dev_plan";
    }

    /**
     * 打开计划项开发与详情页面
     * @param id 营销机会的id
     * @param request request请求
     * @return 视图
     */
    @RequestMapping("toCusDevPlanPage")
    public String toCusDevPlanPage(Integer id, HttpServletRequest request) {

        // 通过id查询营销机会对象
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
        // 将对象设置到请求域中
        request.setAttribute("saleChance", saleChance);

        return "cusDevPlan/cus_dev_plan_data";
    }

    /**
     * 进入添加或修改计划项的页面
     * @param request request请求
     * @param sId 营销机会ID
     * @param id 计划项ID
     * @return 视图
     */
    @RequestMapping("toAddOrUpdateCusDevPlanPage")
    public String toAddOrUpdateCusDevPlanPage(HttpServletRequest request, Integer sId, Integer id) {
        // 将营销机会ID设置到请求域中，给计划项页面获取
        request.setAttribute("sId", sId);

        // 通过计划项ID查询记录
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        // 将计划项数据设置到请求域中
        request.setAttribute("cusDevPlan", cusDevPlan);
        return "cusDevPlan/add_update";
    }

    /***
     * 客户开发计划数据查询（分页多条件查询）
     * @param cusDevPlanQuery cusDevPlanQuery
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery) {
        return cusDevPlanService.queryCUsDevPlanByParams(cusDevPlanQuery);
    }

    /**
     * 添加客户开发计划项数据
     * @param cusDevPlan 客户开发计划实体类
     * @return 消息模型对象
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("计划项添加成功！");
    }

    /**
     * 更新计划项
     * @param cusDevPlan 客户开发计划实体类
     * @return 消息模型对象
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项更新成功！");
    }

    /**
     * 删除计划项
     * @param id 营销机会id
     * @return 消息模型对象
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id) {
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划项更新成功！");
    }


}
