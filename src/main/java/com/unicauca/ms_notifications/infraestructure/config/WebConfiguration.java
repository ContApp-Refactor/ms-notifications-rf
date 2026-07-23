package com.unicauca.ms_notifications.infraestructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.unicauca.ms_notifications.infraestructure.output.multitenancy.interceptor.TenantInterceptor;

/**
 * @brief Web configuration class.
 * Registers web interceptors for handling multi-tenancy.
 */

@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    /**
     * @brief Registers the TenantInterceptor to the interceptor registry.
     * @param registry the interceptor registry 
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(tenantInterceptor);
    }

}
