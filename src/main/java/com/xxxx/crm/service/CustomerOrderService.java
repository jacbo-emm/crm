package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CustomerOrderMapper;
import com.xxxx.crm.query.CustomerOrderQuery;
import com.xxxx.crm.vo.CustomerOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerOrderService extends BaseService<CustomerOrder, Integer> {
    @Resource
    private CustomerOrderMapper customerOrderMapper;

    /**
     * 多条件分页查询客户
     * @param customerOrderQuery 客户订单查询实体类
     * @return map
     */
    public Map<String, Object> queryCustomerOrderByParams(CustomerOrderQuery customerOrderQuery) {
        Map<String, Object> map = new HashMap<>();

        // 开启分页
        PageHelper.startPage(customerOrderQuery.getPage(), customerOrderQuery.getLimit());
        // 得到对应分页对象
        PageInfo<CustomerOrder> pageInfo = new PageInfo<>(customerOrderMapper.selectByParams(customerOrderQuery));

        // 设置map对象
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        // 设置分页好的列表
        map.put("data",pageInfo.getList());

        return map;
    }

    /**
     * 通过订单id查询对应的订单记录
     * @param orderId 订单ID
     * @return map
     */
    public Map<String, Object> queryOrderById(Integer orderId) {
        return customerOrderMapper.queryOrderById(orderId);
    }

}
