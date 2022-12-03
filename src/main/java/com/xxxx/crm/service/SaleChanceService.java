package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分页查询营销机会(返回的格式必须要求layUI中数据表格要求的格式)
     * @param saleChanceQuery SaleChanceQuery类对象
     * @return Map对象
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {
        Map<String, Object> map = new HashMap<>();
        // 开启分页
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        // 得到对应分页对象
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));
        // 设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        // 设置分页好的列表
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加营销机会
     * @param saleChance 营销机会实体类对象
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        // 1.参数校验 调用checkSaleChanceParams()方法
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2.设置相关字段的默认值
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        // 判断是否设置了指派人
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            // state 分配状态
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setAssignTime(null);
            // 设置DevResult开发状态
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        } else {
            // state 分配状态
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            // 设置DevResult开发状态
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }
        // 执行添加操作，判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) < 1, "添加营销机会失败！");
    }

    /**
     * 更新营销机会
     * @param saleChance 营销机会实体类对象
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateChance(SaleChance saleChance) {
        /*1.参数校验*/
        // 营销机会非空，数据库中对应的记录存在
        AssertUtil.isTrue(null == saleChance.getId(), "待更新记录不存在！");
        // 通过主键查询对象
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        // 判断数据库中对应的记录存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        // 调用checkSaleChanceParams()方法
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        /*2.设置相关参数的默认值*/
        // update更新时间，设置为系统当前时间
        saleChance.setUpdateDate(new Date());
        // assignMan指派人
        // 判断原始数据是否存在
        if (StringUtils.isBlank(saleChance.getAssignMan())) { // 不存在
            // 判断修改后的值是否存在
            if (!StringUtils.isBlank(saleChance.getAssignMan())) { // 修改前为空，修改后有值
                // assignTime指派时间设置为当前系统时间
                saleChance.setAssignTime(new Date());
                // 分配状态 1 = 已分配
                saleChance.setState(StateStatus.STATED.getType());
                // 开发状态 1 = 开发中
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        } else { // 存在
            // 判断修改后的值是否存在
            if (StringUtils.isBlank(saleChance.getAssignMan())) { // 修改前有值，修改后无值
                // assignTime指派时间设置为null
                saleChance.setAssignTime(null);
                // 分配状态 0 = 未分配
                saleChance.setState(StateStatus.UNSTATE.getType());
                // 开发状态 0 = 未开发
                saleChance.setDevResult(DevResult.UNDEV.getStatus());
            } else { // 修改前有值，修改后有值
                // 判断修改前后是否是同一个用户
                if (!saleChance.getAssignMan().equals(temp.getAssignMan())) {
                    // 更新指派时间
                    saleChance.setAssignTime(new Date());
                } else {
                    // 设置指派时间为修改前的时间
                    saleChance.setAssignTime(temp.getAssignTime());
                }
            }
        }
        /*执行更新操作，判断受影响的行数*/
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1, "更新营销机会失败！");

    }

    /**
     * 参数校验
     * @param customerName 顾客名
     * @param linkMan 联系人
     * @param linkPhone 联系电话
     */
    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名称不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "联系号码不能为空！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "联系号码格式不正确！");
    }

    /**
     * 删除营销机会
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids) {
        // 判断id是否为空
        AssertUtil.isTrue(null == ids || ids.length < 1, "待删除记录不存在");
        // 执行删除(更新)操作，判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length, "营销机会数据删除失败！");
    }

    /**
     * 更新营销机会的开发状态
     * @param id 营销机会的id
     * @param devResult 开发状态 开发成功 = 2 开发失败 = 3
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        // 判断ID是否为空
        AssertUtil.isTrue(null == id, "待更新记录不存在！");
        // 通过id查询营销机会数据
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        // 判断对象是否为空
        AssertUtil.isTrue(null == saleChance, "待更新记录不存在！");

        // 设置开发状态
        saleChance.setDevResult(devResult);

        // 执行更新操作，判断受影响的函数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1, "开发状态更新失败！");
    }
}
