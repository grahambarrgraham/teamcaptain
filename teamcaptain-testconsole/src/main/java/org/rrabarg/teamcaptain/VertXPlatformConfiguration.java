package org.rrabarg.teamcaptain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

@Configuration
@PropertySource(value = "classpath:vertx.properties")
public class VertXPlatformConfiguration {

    private static final String VERTX_HOSTNAME = "vertx.hostname";

    @Autowired
    Environment environment;

    @Bean
    public EventBus eventBus() {
        // final Vertx vertx = VertxFactory.newVertx(environment.getProperty(VERTX_HOSTNAME));
        // return vertx.eventBus();
        return vertxPlatformManager().vertx().eventBus();
    }

    @Bean
    public PlatformManager vertxPlatformManager() {
        return PlatformLocator.factory.createPlatformManager();
    }
}