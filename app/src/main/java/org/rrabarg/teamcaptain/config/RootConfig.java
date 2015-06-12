package org.rrabarg.teamcaptain.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RootConfig {

    public static void main(String[] args) throws Exception {
        System.setProperty("spring.profiles.active", "inmemory, mutableclock, androidsms, jetty");
        SpringApplication.run(RootConfig.class, args);
    }
}
