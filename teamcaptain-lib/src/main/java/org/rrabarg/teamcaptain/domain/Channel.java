package org.rrabarg.teamcaptain.domain;

public enum Channel {

    Email(ReactorMessageKind.OutboundEmail), Sms(ReactorMessageKind.OutboundSms);

    private ReactorMessageKind outboundMessageKind;

    Channel(ReactorMessageKind kind) {
        this.outboundMessageKind = kind;
    }

    public ReactorMessageKind getOutgoingMessageKind() {
        return outboundMessageKind;
    }
}
