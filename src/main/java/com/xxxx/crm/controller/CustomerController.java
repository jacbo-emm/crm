package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.service.CustomerService;
import com.xxxx.crm.vo.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer")
public class CustomerController extends BaseController {
    @Resource
    private CustomerService customerService;

    /**
     * 进入客户信息管理页面
     * @return 视图
     */
    @RequestMapping("index")
    public String index() {
        return "customer/customer";
    }

    /**
     * 打开添加或修改客户信息的对话框
     * @param id 客户记录的id
     * @param request 请求
     * @return 视图
     */
    @RequestMapping("toAddOrUpdateCustomerPage")
    public String toAddOrUpdateCustomerPage(Integer id, HttpServletRequest request) {
        // 如果id不为空，则查询客户记录
        if (null != id) {
            // 通过id查询客户记录
            Customer customer = customerService.selectByPrimaryKey(id);
            // 将客户记录存到作用域中
            request.setAttribute("customer",customer);
        }
        return "customer/add_update";
    }

    /**
     * 打开客户的订单页面
     * @param customerId 客户id
     * @param model model实体类
     * @return 视图
     */
    @RequestMapping("toCustomerOrderPage")
    public String toCustomerOrderPage(Integer customerId, Model model) {
        // 通过客户ID查询客户记录，设置到请求域中
        model.addAttribute("customer", customerService.selectByPrimaryKey(customerId));
        return "customer/customer_order";
    }

    /**
     * 分页多条件查询客户
     * @param customerQuery 客户查询类
     * @return JSON数据
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerByParams(CustomerQuery customerQuery) {
        return customerService.queryCustomerByParams(customerQuery);
    }


    /**
     * 添加客户
     * @param customer 客户实体类
     * @return ResultInfo JSON数据
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCustomer(Customer customer) {
        customerService.addCustomer(customer);
        return success("添加客户信息成功！");
    }

    /**
     * 更新客户
     * @param customer 客户实体类
     * @return ResultInfo JSON数据
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCustomer(Customer customer) {
        customerService.updateCustomer(customer);
        return success("修改客户信息成功！");
    }

    /**
     * 删除客户
     * @param id 客户记录的id
     * @return ResultInfo-->JSON数据
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCustomer(Integer id) {
        customerService.deleteCustomer(id);
        return success("删除客户信息成功！");
    }

    /**
     * 客户贡献分析
     * @param customerQuery 客户查询类对象
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping("queryCustomerContributionByParams")
    @ResponseBody
    public Map<String,Object> queryCustomerContributionByParams(CustomerQuery customerQuery) {
        return customerService.queryCustomerContributionByParams(customerQuery);
    }

    /**
     * 查询客户构成 （折线图）
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping("countCustomerMake")
    @ResponseBody
    public Map<String, Object> countCustomerMake() {
        return customerService.countCustomerMake();
    }


    /**
     * 查询客户构成 （饼状图）
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping("countCustomerMake02")
    @ResponseBody
    public Map<String, Object> countCustomerMake02() {
        return customerService.countCustomerMake02();
    }




}
