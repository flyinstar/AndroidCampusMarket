package com.campus.trade.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：
 * 1. /images/** 静态资源映射到本地磁盘目录
 * 2. 注册 JWT 拦截器并放行登录/注册等公开路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dir = uploadDir.endsWith("/") || uploadDir.endsWith("\\") ? uploadDir : uploadDir + "/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + dir);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 除 登录/注册(/v1/auth/**) 与 静态图片(/images/**) 外，全部接口都需要登录。
        // 说明：App 登录后才进入主界面，因此商品浏览等接口同样携带 Token。
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/v1/**")
                .excludePathPatterns(
                        "/v1/auth/**",
                        "/error"
                );
    }
}
