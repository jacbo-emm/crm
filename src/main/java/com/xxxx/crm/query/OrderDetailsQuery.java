package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

/**
 * 订单详情查询类
 */
public class OrderDetailsQuery extends BaseQuery {
    private Integer orderId; // 订单ID

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
