package org.rrabarg.teamcaptain.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Email {
    private final String toAddress;
    private final String subject;
    private final String body;
    private final String fromAddress;

    public Email(String subject, String toAddress, String fromAddress, String body) {
        this.subject = subject;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.body = body;
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

}
