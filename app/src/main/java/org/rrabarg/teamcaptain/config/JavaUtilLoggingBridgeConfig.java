package org.rrabarg.teamcaptain.config;

import javax.annotation.PostConstruct;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.stereotype.Component;

@Component
public class JavaUtilLoggingBridgeConfig {

    @PostConstruct
    void setup() {
        SLF4JBridgeHandler.removeHandlersForRootLogger(); // (since SLF4J 1.6.5)
        SLF4JBridgeHandler.install();
    }

}
