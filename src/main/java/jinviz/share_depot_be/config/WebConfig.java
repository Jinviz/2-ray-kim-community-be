package jinviz.share_depot_be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.directory}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 파일에 대한 리소스 핸들러 설정
        registry.addResourceHandler("/" + uploadDir + "/**")
                .addResourceLocations("file:./" + uploadDir + "/");


    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS 설정
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://아무말대잔치.com")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}