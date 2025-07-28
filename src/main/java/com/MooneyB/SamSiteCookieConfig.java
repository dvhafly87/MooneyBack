package com.MooneyB;

import org.springframework.context.annotation.Configuration;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@Configuration
public class SamSiteCookieConfig {
	
	@Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies("None"); // "Lax", "Strict"도 가능
            context.setCookieProcessor(cookieProcessor);
        });
    }
}
