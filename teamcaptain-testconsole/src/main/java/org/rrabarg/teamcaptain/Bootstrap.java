package org.rrabarg.teamcaptain;

import org.rrabarg.teamcaptain.demo.CompetitionBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws InterruptedException {

        System.setProperty("spring.profiles.active", "inmemory, mutableclock");

        new ClassPathXmlApplicationContext("applicationContext.xml");

        new CompetitionBuilder().build();

        final Object lock = new Object();
        synchronized (lock) {
            lock.wait();
        }
    }

}
