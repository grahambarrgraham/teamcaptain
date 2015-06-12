package org.rrabarg.teamcaptain.channel;

import java.time.Instant;

import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.domain.Channel;

public class Email extends Message {

    public Email(String subject, String targetIdentity, String sourceIdentity, String body, Instant instant) {
        super(subject, targetIdentity, sourceIdentity, body, instant, Channel.Email);
    }

}
