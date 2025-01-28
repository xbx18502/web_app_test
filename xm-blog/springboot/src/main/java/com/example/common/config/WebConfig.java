package com.example.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/register")
                .excludePathPatterns("/api/files/**")
                .excludePathPatterns("/api/blog/selectById/*")
                .excludePathPatterns("/api/blog/selectAll")
                .excludePathPatterns("/api/blog/selectPage")
                .excludePathPatterns("/api/blog/selectTop")
                .excludePathPatterns("/api/blog/selectRecommend/*")
                .excludePathPatterns("/api/files/**")
                .excludePathPatterns("/api/category/selectById/*")
                .excludePathPatterns("/api/category/selectAll")
                .excludePathPatterns("/api/notice/selectById/*")
                .excludePathPatterns("/api/notice/selectAll")
                .excludePathPatterns("/api/notice/selectPage")
                .excludePathPatterns("/api/activity/selectTop")
                .excludePathPatterns("/api/activity/selectById/*");
    }
}