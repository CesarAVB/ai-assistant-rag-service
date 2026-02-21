package br.com.cesaravb.rag_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

// ========================================
// CorsConfig - Configura as origens permitidas
// para requisicoes cross-origin do frontend.
// CorsFilter tem prioridade maior que o AuthFilter,
// garantindo que o preflight OPTIONS seja liberado
// antes de qualquer validacao de token.
// ========================================
@Configuration
@Slf4j
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    // ========================================
    // corsFilter - Define quais origens, metodos
    // e headers sao permitidos nas requisicoes.
    // ========================================
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
    	
    	log.info("CORS allowed origins: {}", allowedOrigins);
        var config = new CorsConfiguration();

        for (String origin : allowedOrigins.split(",")) {
            config.addAllowedOrigin(origin.trim());
        }

        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}