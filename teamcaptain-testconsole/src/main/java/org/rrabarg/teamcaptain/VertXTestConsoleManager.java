package org.rrabarg.teamcaptain;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.PlatformManager;

@Component
@PropertySource(value = "classpath:vertx.properties")
public class VertXTestConsoleManager {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    PlatformManager vertxPlatformManager;

    private static final String VERTX_CONSOLE_MODULE = "org.rrabarg~teamcaptain.testconsole~0.0.1-SNAPSHOT";

    @PostConstruct
    void deployVertxConsoleModule() {

        final JsonObject conf = new JsonObject().putString("foo", "wibble");

        vertxPlatformManager.deployModuleFromClasspath(VERTX_CONSOLE_MODULE, conf, 1, new URL[] {},

                asyncResult -> {
                    if (asyncResult.succeeded()) {
                        logger.info("Successfully deployed vertx module {}, with Deployment ID {}",
                                VERTX_CONSOLE_MODULE,
                                asyncResult.result());
                    } else {
                        logger.error("Failed to deploy vertx module {}, error code was {}", VERTX_CONSOLE_MODULE,
                                asyncResult.cause());

                        throw new IllegalStateException("Failed to deploy vertx console module");
                    }
                });
    }

}
