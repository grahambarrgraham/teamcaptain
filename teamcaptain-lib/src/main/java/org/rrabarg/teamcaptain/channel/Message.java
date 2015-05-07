package org.rrabarg.teamcaptain.channel;

import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.rrabarg.teamcaptain.domain.Channel;

public class Message {

    private final String targetIdentity;
    private final String subject;
    private final String body;
    private final String sourceIdentity;
    private final Instant instant;
    private final Channel channel;

    public Message(String subject, String targetIdentity, String sourceIdentity, String body, Instant instant,
            Channel channel) {
        this.subject = subject;
        this.targetIdentity = targetIdentity;
        this.sourceIdentity = sourceIdentity;
        this.body = body;
        this.instant = instant;
        this.channel = channel;
    }

    public Message(Message message, Instant instant) {
        this(message.subject, message.targetIdentity, message.sourceIdentity, message.body, instant, message.channel);
    }

    public String getSubject() {
        return subject;
    }

    public String getToAddress() {
        return targetIdentity;
    }

    public String getFromAddress() {
        return sourceIdentity;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_FIELD_NAMES_STYLE);
    }

    public Instant getTimestamp() {
        return instant;
    }

    public String getSourceIdentity() {
        return getFromAddress();
    }

    public String getTargetIdentity() {
        return getToAddress();
    }

    public Channel getChannel() {
        return channel;
    }

    public Message withTimestamp(Instant instant) {
        return new Message(this, instant);
    }

}
