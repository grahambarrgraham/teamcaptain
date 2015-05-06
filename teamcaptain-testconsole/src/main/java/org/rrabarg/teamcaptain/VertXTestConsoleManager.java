package org.rrabarg.teamcaptain;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

                // vertxPlatformManager.deployVerticle(WebserverVerticle.class.getName(), conf, getSystemClasspath(), 1,
                // null,
                asyncResult -> {
                    // vertxPlatformManager.deployModule(VERTX_CONSOLE_MODULE, conf, 1, asyncResult -> {
                if (asyncResult.succeeded()) {
                    logger.info("Successfully deployed vertx module {}, with Deployment ID {}", VERTX_CONSOLE_MODULE,
                            asyncResult.result());
                } else {
                    logger.error("Failed to deploy vertx module {}, error code was {}", VERTX_CONSOLE_MODULE,
                            asyncResult.cause());

                    throw new IllegalStateException("Failed to deploy vertx console module");
                }
            });
    }

    private URL[] getSystemClasspath() {
        final ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        // Get the URLs
        final URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
        final List<URL> asList = new ArrayList(Arrays.asList(urls));
        asList.removeIf(a -> a.getPath().endsWith("jar"));
        return asList.toArray(new URL[asList.size()]);

    }
}
