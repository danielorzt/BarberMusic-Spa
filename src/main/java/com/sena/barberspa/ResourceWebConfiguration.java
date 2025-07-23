package com.sena.barberspa;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceWebConfiguration implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Ruta para servir imágenes desde recursos estáticos
		registry.addResourceHandler("/images/**")
				.addResourceLocations("classpath:/static/assets/img/")
				.setCachePeriod(31556926);

		// Ruta para servir dependencias de Node.js desde directorio local
		registry.addResourceHandler("/node_modules/**")
				.addResourceLocations("file:./node_modules/")
				.setCachePeriod(3600);
	}

}
