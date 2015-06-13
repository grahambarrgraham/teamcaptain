package org.rrabarg.teamcaptain.testconsole;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

@Configuration
@Profile("chatconsole")
public class VertXPlatformConfiguration {

    @Bean
    public Vertx vertx() {
        return vertxPlatformManager().vertx();
    }

    @Bean
    public EventBus eventBus() {
        return vertx().eventBus();
    }

    @Bean
    public PlatformManager vertxPlatformManager() {
        return PlatformLocator.factory.createPlatformManager();
    }
}