package org.rrabarg.teamcaptain.service;

@SuppressWarnings("serial")
public class ScheduleException extends RuntimeException {

    public ScheduleException(String string, Exception e) {
        super(string, e);
    }
}
