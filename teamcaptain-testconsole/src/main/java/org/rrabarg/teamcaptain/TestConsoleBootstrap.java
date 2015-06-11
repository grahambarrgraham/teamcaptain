package org.rrabarg.teamcaptain;

public class TestConsoleBootstrap {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws InterruptedException {
        TeamCaptainBootstrap.bootstrap("inmemory", "mutableclock", "androidsms", "jetty");
    }

}
