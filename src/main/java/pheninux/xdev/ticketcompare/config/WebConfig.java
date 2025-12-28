package pheninux.xdev.ticketcompare.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pheninux.xdev.ticketcompare.interceptor.LicenseCheckInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LicenseCheckInterceptor licenseCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(licenseCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/**",
                    "/pricing",
                    "/payment/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/error",
                    "/actuator/**"
                );
    }
}

