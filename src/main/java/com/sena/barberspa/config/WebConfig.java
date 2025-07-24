package com.sena.barberspa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // SOLUCION CRITICA: Aumentar timeout para evitar ERR_INCOMPLETE_CHUNKED_ENCODING
        configurer.setDefaultTimeout(30000); // 30 segundos
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuración específica para assets
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(31556926)
                .resourceChain(true);
        
        // Configuración para otros recursos estáticos
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(31556926)
                .resourceChain(true);
    }
}