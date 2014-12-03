package org.rrabarg.teamcaptain.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Email {
    private final String address;
    private final String subject;
    private final String body;

    public Email(String subject, String toAddress, String fromAddress, String body) {
        this.subject = subject;
        this.address = toAddress;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_FIELD_NAMES_STYLE);
    }

}
