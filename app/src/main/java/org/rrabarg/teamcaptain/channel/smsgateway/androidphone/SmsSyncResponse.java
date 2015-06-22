package org.rrabarg.teamcaptain.channel.smsgateway.androidphone;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.rrabarg.teamcaptain.channel.SmsMessage;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsSyncResponse {

    private Payload payload;

    public SmsSyncResponse(boolean success, String error) {
        this.payload = new Payload(success, error);
    }

    public SmsSyncResponse(String secretKey, Collection<SmsMessage> messages) {
        this.payload = new Payload(secretKey, messages);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Payload getPayload() {
        return payload;
    }

    public class Payload {

        private boolean success;
        private String error;
        private String secretKey;
        private String task;
        private List<OutgoingSyncMessages> messages;

        public Payload(boolean success, String error) {
            this.success = success;
            this.error = error;
        }

        public Payload(String secretKey, Collection<SmsMessage> messages) {
            this.task = "send";
            this.success = true;
            this.secretKey = secretKey;
            this.messages = messages.stream().map(m -> new OutgoingSyncMessages(m)).collect(Collectors.toList());
        }

        public boolean getSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }

        public String getTask() {
            return task;
        }

        public String getSecretKey() {
            return secretKey;
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

            public String getTo() {
                return to;
            }

            public String getMessage() {
                return message;
            }

            public UUID getUuid() {
                return uuid;
            }
        }

        public List<OutgoingSyncMessages> getMessages() {
            return messages;
        }
    }
}
