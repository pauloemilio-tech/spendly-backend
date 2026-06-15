package com.spendly.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebConfigTests {

    @Test
    void allowsConfiguredOriginsMethodsAndHeaders() {
        WebConfig webConfig = new WebConfig(
                "http://localhost:5173, http://localhost:5174, https://spendly-fawn.vercel.app"
        );
        CorsConfigurationSource source = webConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/auth/login");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins()).containsExactly(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://spendly-fawn.vercel.app"
        );
        assertThat(configuration.getAllowedMethods()).containsExactlyElementsOf(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        assertThat(configuration.getAllowedHeaders()).containsExactly("*");
        assertThat(configuration.checkOrigin("https://spendly-fawn.vercel.app"))
                .isEqualTo("https://spendly-fawn.vercel.app");
    }
}
