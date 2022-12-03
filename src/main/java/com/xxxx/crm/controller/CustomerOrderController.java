package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.query.CustomerOrderQuery;
import com.xxxx.crm.service.CustomerOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("order")
public class CustomerOrderController extends BaseController {
    @Resource
    private CustomerOrderService customerOrderService;

    /**
     * 打开订单详情的页面
     * @param orderId 订单id
     * @param model model实体类
     * @return 视图
     */
    @RequestMapping("toOrderDetailPage")
    public String toOrderDetailPage(Integer orderId, Model model) {

        // 通过订单ID查询对应的订单记录
        Map<String,Object> map = customerOrderService.queryOrderById(orderId);
        // 将数据设置到请求域中
        model.addAttribute("order",map);

        return "customer/customer_order_detail";
    }

    /**
     * 多条件分页查询客户
     * @param customerOrderQuery 客户订单查询实体类
     * @return JSON数据
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerOrderByParams(CustomerOrderQuery customerOrderQuery) {
        return customerOrderService.queryCustomerOrderByParams(customerOrderQuery);
    }



}
