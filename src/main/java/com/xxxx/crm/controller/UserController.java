package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController{
    @Resource
    private UserService userService;

    /**
     * 用户登录
     * @param userName 用户姓名
     * @param userPwd 用户密码
     * @return ResultInfo对象
     */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();
        // 调用service层登录方法
        UserModel userModel = userService.userLogin(userName, userPwd);
        // 设置resultInfo的result值(将数据返回给请求)
        resultInfo.setResult(userModel);

        return resultInfo;
    }

    /**
     * 修改密码
     * @param request request请求
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param repeatPwd 确认密码
     * @return
     */
    @RequestMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPwd, String newPwd, String repeatPwd) {
        ResultInfo resultInfo = new ResultInfo();
        // 获取cookie中的userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调用service层修改密码方法
        userService.updatePassword(userId, oldPwd, newPwd, repeatPwd);

        return resultInfo;
    }

    // 进入修改密码的页面
    @RequestMapping("toPasswordPage")
    public String toPasswordPage() {
        return "user/password";
    }

    // 进入用户列表页面
    @RequestMapping("index")
    public String index() {
        return "user/user";
    }

    /**
     * 进入打开添加或修改用户的页面
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("toAddOrUpdateUserPage")
    public String toAddOrUpdateUserPage(Integer id, HttpServletRequest request) {
        // 判断id是否为空，不为空表示更新操作，查询用户对象
        if (id != null) {
            // 通过id查询用户对象
            User user = userService.selectByPrimaryKey(id);
            // 将数据设置到请求域中
            request.setAttribute("userInfo",user);
        }
        return "user/add_update";
    }


    /**
     * 查询所有销售人员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }

    /**
     * 分页多条件查询用户列表
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> selectByParams(UserQuery userQuery) {
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user) {
        userService.addUser(user);
        return success("用户添加成功！");
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("用户更新成功！");
    }

    /**
     * 用户删除
     * @param ids
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids) {

        userService.deleteByIds(ids);

        return success("用户删除成功！");
    }

    /**
     * 查询所有的客户经理
     */
    @RequestMapping("queryAllCustomerManagers")
    @ResponseBody
    public List<Map<String,Object>> queryAllCustomerManagers(){
        return userService.queryAllCustomerManagers();
    }
}
