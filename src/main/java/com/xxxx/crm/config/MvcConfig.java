package com.xxxx.crm.config;

import com.xxxx.crm.interceptor.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 配置类
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    // 将方法的返回值交给IOC维护
    @Bean
    public NoLoginInterceptor noLoginInterceptor() {
        return new NoLoginInterceptor();
    }

    /**
     * 拦截器(Interceptor)同 Filter 过滤器一样，它俩都是面向切面编程——AOP 的具体实现
     *
     * @param registry 拦截器对象
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 需要一个实现了拦截器功能的实例对象
        registry.addInterceptor(noLoginInterceptor())
                // 设置需要被拦截的资源 默认拦截所以资源
                .addPathPatterns("/**")
                // 设置不需要被拦截的资源
                .excludePathPatterns("/css/**", "/images/**", "/js/**", "/lib/**", "/index", "/user/login");
    }
}
