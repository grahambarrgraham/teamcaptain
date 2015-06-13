package org.rrabarg.teamcaptain;

import org.rrabarg.teamcaptain.config.JavaUtilLoggingBridgeConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.inject.Inject;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = false)
@ComponentScan(basePackages = "org.rrabarg.teamcaptain, org.vertx.java.core")
public class TeamCaptainApplication {

    @Inject
    JavaUtilLoggingBridgeConfig julBridge; // ensure configured

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TeamCaptainApplication.class, args);
    }

}
