package org.rrabarg.teamcaptain.channel.smsgateway.androidphone;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.rrabarg.teamcaptain.channel.SmsMessage;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by graham on 09/06/15.
 */
@JsonTypeName("payload")
public class SmsSyncResponse {

    private boolean success;
    private String error;
    private String secretKey;
    private String task;
    private List<OutgoingSyncMessages> messages;

    public SmsSyncResponse(@JsonProperty("success") boolean success, @JsonProperty("error") String error) {
        this.success = success;
        this.error = error;
    }

    public SmsSyncResponse(String secretKey, Stream<SmsMessage> stream) {
        task = "send";
        this.secretKey = secretKey;
        this.messages = stream.map(m -> new OutgoingSyncMessages(m)).collect(Collectors.toList());

    }

    public boolean getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public class OutgoingSyncMessages {

        private final String to;
        private final String message;
        private final UUID uuid;

        public OutgoingSyncMessages(SmsMessage message) {
            this.to = message.getToAddress();
            this.message = message.getBody();
            this.uuid = UUID.randomUUID();
        }
    }
}
