package omnicanal.gateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

       // EN PRODUCTION: je reviens vers cela pour  l'adapter
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("Authorization");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");

        config.setMaxAge(3600L);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
