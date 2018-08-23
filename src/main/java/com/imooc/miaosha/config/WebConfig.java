package com.imooc.miaosha.config;

import com.imooc.miaosha.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;


/**
 * mvc 的相关配置
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{


    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Autowired
    AccessInterceptor accessInterceptor;

    /**
     * 这个方法，实现了controller中参数可直接添加 诸如model，request等。同样的，可以用他来实现添加 user
     * @param argumentResolvers 是个List<HandlerMethodArgumentResolver>，所以我们把需要实现的UserArgumentResolvers加进去
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    /**
     * 拦截器的注册  WebMvcConfigurerAdapter中的addInterceptors方法
     * 注册上我们 自定义的拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }

}
