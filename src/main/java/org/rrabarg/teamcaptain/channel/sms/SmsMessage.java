package org.rrabarg.teamcaptain.channel.sms;

import java.time.Instant;

import org.rrabarg.teamcaptain.channel.InboundMessage;

public class SmsMessage implements InboundMessage {

    private final String phone;
    private final String text;
    private final Instant instant;

    public SmsMessage(String phone, String text, Instant instant) {
        this.phone = phone;
        this.text = text;
        this.instant = instant;
    }

    public String getPhone() {
        return phone;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getSourceIdentity() {
        return getPhone();
    }

    @Override
    public String getBody() {
        return getText();
    }

    public Instant getInstant() {
        return instant;
    }

}
