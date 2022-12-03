package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     * @param userName 用户名
     * @param userPwd 用户密码
     */
    public UserModel userLogin(String userName, String userPwd) {
        // 1.参数判断，判断用户名，用户密码非空
        checkLoginParams(userName, userPwd);
        // 2.调用数据访问层
        User user = userMapper.queryUserByName(userName);
        // 3.判断用户对象是否为空
        AssertUtil.isTrue(null == user, "用户姓名不存在！");
        // 4.判断密码是否正确
        checkUserPwd(userPwd, user.getUserPwd());
        // 5.返回构建的用户对象
        return buildUserInfo(user);
    }

    /**
     * 参数判断
     * @param userName 用户名
     * @param userPwd 用户密码
     */
    private void checkLoginParams(String userName, String userPwd) {
        // 验证用户姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户姓名不能为空！");
        // 验证用户密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空！");

    }

    /**
     * 构建需要返回给客户端的用户对象
     * @param user User对象
     * @return
     */
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        // userModel.setUserId(user.getId());
        // 设置加密的用户id
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 修改密码
     * @param userId 用户id
     * @param oldPwd 原始密码
     * @param newPwd 新密码
     * @param repeatPwd 确认密码
     */
    @Transactional(propagation = Propagation.REQUIRED) // 开启事务
    public void updatePassword(Integer userId, String oldPwd, String newPwd, String repeatPwd) {
        // 通过用户id查询用户记录，返回用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        // 判断用户记录是否存在
        AssertUtil.isTrue(null == user, "待更新记录不存在！");
        // 参数校验
        checkPasswordParams(user, oldPwd, newPwd, repeatPwd);
        // 设置用户的新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        // 执行更新，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "修改密码失败！");
    }

    /**
     * 修改密码的参数校验
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        // 判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "原始密码不能为空！");
        // 判断原始密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)), "原始密码不正确！");
        // 判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空！");
        // 判断新密码是否与原始密码一致
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码不能与原始密码相同！");
        // 判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd), "确认密码不能为空！");
        // 判断确认密码是否与新密码一致
        AssertUtil.isTrue(!newPwd.equals(repeatPwd), "确认密码与新密码不一致！");
    }

    /**
     * 密码判断 先将客户端传递过来的密码加密，在与数据库中查询到的密码作比较
     * @param userPwd 用户输入的密码
     * @param pwd 数据库中的密码
     */
    private void checkUserPwd(String userPwd, String pwd) {
        // 将客户端传递过来的密码加密
        userPwd = Md5Util.encode(userPwd);
        // 判断密码是否相等
        AssertUtil.isTrue(!userPwd.equals(pwd), "用户密码不正确！");

    }

    /**
     * 查询所有销售人员
     * @return List集合对象
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    /**
     * 添加用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
        /*1.参数校验*/
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), null);
        /*2.设置参数的默认值*/
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        // 设置默认密码(需要通过Md5Util工具类加密)
        user.setUserPwd(Md5Util.encode("123456"));
        /*3.执行添加操作，判断受影响的行数*/
        AssertUtil.isTrue(userMapper.insertSelective(user) < 1, "用户添加失败！");
        /* 用户角色关联 */
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 更新用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        // 1.判断用户ID是否为空，且数据存在
        AssertUtil.isTrue(null == user.getId(), "待更新记录不存在！");
        // 1.1 通过id查询数据
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 1.2 判断用户是否存在
        AssertUtil.isTrue(null == temp, "待更新的记录不存在！");
        // 2.参数校验
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), user.getId());
        // 3.设置默认值
        user.setUpdateDate(new Date());
        // 4.执行更新操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户更新失败！");
        /* 用户角色关联 */
        relationUserRole(user.getId(), user.getRoleIds());

    }

    /**
     * 删除用户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByIds(Integer[] ids) {
        // 判断ids是否为空，长度是否大于0
        AssertUtil.isTrue(ids == null || ids.length == 0, "待删除记录不存在！");
        // 执行删除操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length, "用户删除失败！");
        for (Integer userId : ids) {
            // 通过用户ID查询对应的用户角色记录
            Integer count  = userRoleMapper.countUserRoleByUserId(userId);
            // 判断角色记录是否存在
            if (count > 0) {
                // 2.1 如果角色记录存在，则删除该用户对应的角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败！");
            }
        }
    }

    /**
     * 用户角色关联
     *  添加操作
     *      原始角色不存在
     *          1. 不添加新的角色记录    不操作用户角色表
     *          2. 添加新的角色记录      给指定用户绑定相关的角色记录
     *  更新操作
     *      原始角色不存在
     *          1. 不添加新的角色记录     不操作用户角色表
     *          2. 添加新的角色记录       给指定用户绑定相关的角色记录
     *      原始角色存在
     *          1. 添加新的角色记录       判断已有的角色记录不添加，添加没有的角色记录
     *          2. 清空所有的角色记录     删除用户绑定角色记录
     *          3. 移除部分角色记录       删除不存在的角色记录，存在的角色记录保留
     *          4. 移除部分角色，添加新的角色    删除不存在的角色记录，存在的角色记录保留，添加新的角色
     *  删除操作
     *      删除指定用户绑定的角色记录
     *  如何进行角色分配？？？
     *      判断用户对应的角色记录存在，先将用户原有的角色记录删除，再添加新的角色记录
     * @param userId  用户ID
     * @param roleIds 角色ID
     */
    private void relationUserRole(Integer userId, String roleIds) {
        // 1.通过用户ID查询角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        // 2.判断角色记录是否存在
        if (count > 0) {
            // 2.1 如果角色记录存在，则删除该用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败！");
        }
        // 3.判断角色ID是否存在，如果存在，则添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)) {
            // 3.1 将用户角色数据设置到集合中，执行批量添加
            List<UserRole> userRoles = new ArrayList<>();
            // 3.2 将角色ID字符串转换成数组
            String[] roleIdsArray = roleIds.split(",");
            // 3.3 遍历数组，得到对应的用户角色对象，并设置到集合中
            for (String roleId : roleIdsArray) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                // 3.3.1 设置到集合中
                userRoles.add(userRole);
            }
            // 3.4 批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles) != userRoles.size(), "用户角色分配失败！");
        }
    }

    /**
     * 对用户操作的参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone, Integer userId) {
        // 1 用户名非空
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空！");
        // 2 判断用户名的唯一性
        // 2.1 通过用户名查询用户对象
        User user = userMapper.queryUserByName(userName);
        // 2.2 添加操作--如果用户名为空，则表示用户名可用。反之则不可用
        // 2.2 修改操作--数据库中有对应的记录，通过用户名查到数据，可能是当前记录本身，也可能是别的记录
        // 2.2 如果用户名存在，且与当前修改记录不是同一个，则表示其他记录占用了该用户名，不可用
        AssertUtil.isTrue(null != user && !(user.getId().equals(userId)), "用户名已存在，请重新输入！");
        // 3 邮箱非空
        AssertUtil.isTrue(StringUtils.isBlank(email), "用户邮箱不能为空！");
        // 4 手机号非空
        AssertUtil.isTrue(StringUtils.isBlank(phone), "用户手机号不能为空！");
        // 5 手机号格式 判断
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号格式不正确！");
    }

    /**
     * 查询所有的客户经理
     */
    public List<Map<String, Object>> queryAllCustomerManagers() {
        return userMapper.queryAllCustomerManagers();
    }


}
