package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.AuthException;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 全局异常统一处理
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /**
     * 异常处理方法
     * 1.返回视图  2.返回JSON数据
     *
     * @param request  request 对象
     * @param response response 对象
     * @param handler  方法对象
     * @param e        异常对象
     * @return ModelAndView
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        // 非法请求拦截
        if (e instanceof NoLoginException) {
            // 重定向到登录页面
            return new ModelAndView("redirect:/index");
        }

        // 设置默认异常处理(返回视图)
        ModelAndView modelAndView = new ModelAndView();
        // 设置异常异常信息
        modelAndView.addObject("code", 500);
        modelAndView.addObject("msg", "系统异常，请重试");
        // 判断HandlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法上声明的@ResponseBody注解对象
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);

            // 判断ResponseBody对象是否为空(为空返回视图，不为空返回JSON数据)
            if (responseBody == null) {
                // 方法返回视图
                // 判断异常类型
                if (e instanceof ParamsException) {
                    ParamsException p = (ParamsException) e;
                    // 设置异常信息
                    modelAndView.addObject("code", p.getCode());
                    modelAndView.addObject("msg", p.getMsg());
                } else if (e instanceof AuthException) { // 认证异常
                    AuthException a = (AuthException) e;
                    // 设置异常信息
                    modelAndView.addObject("code", a.getCode());
                    modelAndView.addObject("msg", a.getMsg());
                }
                return modelAndView;
            } else {
                //方法返回JSON数据
                // 设置默认异常处理
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常，请重试");
                // 判断异常是否是自定义异常
                if (e instanceof ParamsException) {
                    ParamsException p = (ParamsException) e;
                    // 设置异常信息
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                } else if (e instanceof AuthException) { // 认证异常
                    AuthException a = (AuthException) e;
                    resultInfo.setCode(a.getCode());
                    resultInfo.setMsg(a.getMsg());
                }
                // 设置响应类型及编码格式
                response.setContentType("application/json;charset=UTF-8");
                // 得到字符输出流
                try (PrintWriter out = response.getWriter()) {
                    // 将需要返回的对象转换成JSON格式的字符串
                    String json = JSON.toJSONString(resultInfo);
                    out.flush();
                    // 输出数据
                    out.write(json);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }
        return modelAndView;
    }
}
