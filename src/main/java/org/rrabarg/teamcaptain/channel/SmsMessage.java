package org.rrabarg.teamcaptain.channel;

import java.time.Instant;

import org.rrabarg.teamcaptain.domain.Channel;

public class SmsMessage extends Message {

    public SmsMessage(String targetIdentity, String body, Instant instant) {
        super(null, targetIdentity, null, body, instant, Channel.Sms);
    }

}
