package org.rrabarg.teamcaptain;

import org.apache.log4j.Logger;
import org.omg.CORBA.Environment;
import org.rrabarg.teamcaptain.config.JavaUtilLoggingBridgeConfig;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.inject.Inject;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan
public class TeamCaptainApplication {

    @Inject
    JavaUtilLoggingBridgeConfig julBridge; // ensure configured

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TeamCaptainApplication.class, args);
    }

}
