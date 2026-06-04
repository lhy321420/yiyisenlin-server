package com.example.demo.config;

import com.example.demo.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 固定只放行你的github pages域名，本地localhost调试也加上
                .allowedOriginPatterns("https://lhy321420.github.io", "http://localhost:*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 【不动】原有图片上传、静态资源配置
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/upload/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    // 【不动】登录拦截器全部原有配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login.html",
                        "/register.html",
                        "/user/login",
                        "/user/register",
                        "/",
                        "/index.html",
                        "/user-info.html",
                        "/user/update-info",
                        "/**/*.html",
                        "/static/**",
                        "/city/**",
                        "/upload/**"
                );
    }
}