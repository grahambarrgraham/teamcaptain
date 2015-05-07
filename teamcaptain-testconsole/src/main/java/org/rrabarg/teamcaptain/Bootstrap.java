package org.rrabarg.teamcaptain;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws InterruptedException {

    	System.setProperty("spring.profiles.active", "inmemory");
    	
        new ClassPathXmlApplicationContext("applicationContext.xml");

        final Object lock = new Object();
        synchronized (lock) {
            lock.wait();
        }
    }

}
