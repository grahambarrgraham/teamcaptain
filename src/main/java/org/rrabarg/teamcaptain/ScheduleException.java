package org.rrabarg.teamcaptain;

@SuppressWarnings("serial")
public class ScheduleException extends RuntimeException {

    public ScheduleException(String string, Exception e) {
        super(string, e);
    }
}
