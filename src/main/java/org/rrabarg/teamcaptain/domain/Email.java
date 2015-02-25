package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Email {
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

}
