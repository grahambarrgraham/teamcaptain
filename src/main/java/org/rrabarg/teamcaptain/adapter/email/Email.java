package org.rrabarg.teamcaptain.adapter.email;

import java.time.Instant;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.rrabarg.teamcaptain.adapter.InboundMessage;

public class Email implements InboundMessage {
    private final String toAddress;
    private final String subject;
    private final String body;
    private final String fromAddress;
    private final Instant instant;

    public Email(String subject, String toAddress, String fromAddress, String body, Instant instant) {
        this.subject = subject;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.body = body;
        this.instant = instant;
    }

    public String getSubject() {
        return subject;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    @Override
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

    @Override
    public String getSourceIdentity() {
        return getFromAddress();
    }

}
