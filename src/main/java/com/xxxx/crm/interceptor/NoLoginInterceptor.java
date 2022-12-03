package com.xxxx.crm.interceptor;

import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法访问拦截
 */
public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private UserMapper userMapper;
    /**
     * 拦截用户是否是登录状态
     * 在目标方法执行前执行的方法
     * @param request request 对象
     * @param response  response 对象
     * @param handler 方法对象
     * @return boolean值
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie中的用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 判断用户id是否为空，且数据库中存在该id的用户记录
        if (userMapper.selectByPrimaryKey(userId) == null) {
            // 抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
