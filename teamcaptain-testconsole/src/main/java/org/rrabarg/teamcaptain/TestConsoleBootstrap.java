package org.rrabarg.teamcaptain;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class TestConsoleBootstrap {

    private static boolean webApplicationContextInitialized;

    @SuppressWarnings("resource")
    public static void main(String[] args) throws InterruptedException {
        bootstrap("inmemory", "mutableclock", "androidsms", "jetty");
    }

    public static void bootstrap(String... profiles) {

        System.setProperty("spring.profiles.active", String.join(",", profiles));

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();

        applicationContext
                .addApplicationListener(
                        new ApplicationListener<ContextRefreshedEvent>() {
                            @Override
                            public void onApplicationEvent(
                                    ContextRefreshedEvent event) {
                                ApplicationContext ctx =
                                        event.getApplicationContext();
                                if (ctx instanceof GenericWebApplicationContext) {
                                    webApplicationContextInitialized = true;
                                }
                            }
                        });

        applicationContext.registerShutdownHook();
        applicationContext.setConfigLocation("applicationContext.xml");
        applicationContext.refresh();

        if (!webApplicationContextInitialized) {
            System.exit(1);
        }


    }
}