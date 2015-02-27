package org.rrabarg.teamcaptain.channel;

public interface InboundMessage {

    String getSourceIdentity();

    String getBody();

}
