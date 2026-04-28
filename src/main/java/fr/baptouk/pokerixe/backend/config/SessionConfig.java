package fr.baptouk.pokerixe.backend.config;

import jakarta.servlet.SessionCookieConfig;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {


    @Bean
    public ServletContextInitializer sessionCookieConfig() {
        return servletContext -> {
            SessionCookieConfig cookieConfig = servletContext.getSessionCookieConfig();
            cookieConfig.setHttpOnly(true);
            cookieConfig.setSecure(true);
        };
    }
}
