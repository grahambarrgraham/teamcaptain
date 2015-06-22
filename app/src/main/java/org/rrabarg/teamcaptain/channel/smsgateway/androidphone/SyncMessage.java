package org.rrabarg.teamcaptain.channel.smsgateway.androidphone;

public class SyncMessage {

    private String from;
    private String message;
    private String messageId;
    private String sent_to;
    private String secret;
    private String device_id;
    private String unixtimestamp;

    public SyncMessage() {
    }

    public SyncMessage(String from, String message, String messageId, String sent_to, String secret, String device_id, String unixtimestamp) {
        this.from = from;
        this.message = message;
        this.messageId = messageId;
        this.sent_to = sent_to;
        this.secret = secret;
        this.device_id = device_id;
        this.unixtimestamp = unixtimestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSent_to() {
        return sent_to;
    }

    public String getSecret() {
        return secret;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getUnixtimestamp() {
        return unixtimestamp;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
