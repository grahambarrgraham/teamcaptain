package org.rrabarg.teamcaptain.testconsole;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.config.MutableClockFactory;
import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.ReactorMessageKind;
import org.rrabarg.teamcaptain.service.TeamCaptainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static reactor.event.selector.Selectors.$;

@Component
@Profile("chatconsole")
public class ReactorVertxBridge implements Consumer<Event<Message>> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    static final String VERTX_BRIDGE_ADDRESS = "reactorvertxbridge";
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    final Pattern smsMessagePattern = Pattern.compile("(\\d+) (.+)");
    final Pattern emailMessagePattern = Pattern.compile("(.*\\@.*) (.+)");
    final Pattern setDateMessagePattern = Pattern.compile("setDate (.+)");
    final Pattern getDateMessagePattern = Pattern.compile("getDate");

    @Inject
    Reactor reactor;

    @Inject
    MutableClockFactory mutableClockFactory;

    @Inject
    TeamCaptainManager teamCaptainManager;

    @Inject
    EventBus eventBus;

    @Inject
    Vertx vertx;

    @PostConstruct
    public void configure() {

        log.info("Configuring reactor vertx bridge for chat console");

        reactor.on($(ReactorMessageKind.OutboundEmail), this);
        reactor.on($(ReactorMessageKind.OutboundSms), this);

        eventBus.registerHandler(VERTX_BRIDGE_ADDRESS, event -> {
            final Message message = processVertxMessage((String) event.body());

            if (message != null) {
                reactor.notify(ReactorMessageKind.InboundChannelMessage,
                        new Event<>(message));
            }
        });

    }

    @Override
    public void accept(Event<Message> event) {
        final Message message = event.getData();
        try {
            publishToVertxChat(createJsonMessage(getSubjectAndBodyText(message), message.getTargetIdentity()));
        } catch (final JsonProcessingException e) {
            log.error("Could not convert {} to vertx chat json message", event, e);
        }
    }

    private String getSubjectAndBodyText(final Message message) {
        if (message.getSubject() == null) {
            return message.getBody();
        }

        return "Subject :" + message.getSubject() + " Body :" + message.getBody();
    }

    private Message processVertxMessage(String jsonMsg) {

        JsonNode node;
        try {
            node = new ObjectMapper().readTree(jsonMsg);
        } catch (final IOException e) {
            log.warn("Cannot parse {} as json", jsonMsg, e);
            return null;
        }

        final String msg = node.get("message").asText();

        if (processSetDateInstruction(msg) || processGetDateInstruction(msg)) {
            return null;
        }

        Optional<Message> message = processSmsMessage(msg);
        if (message.isPresent()) {
            return message.get();
        }

        message = processEmailMessage(msg);
        if (message.isPresent()) {
            return message.get();
        }

        log.warn("Ignoring message : {}", msg);

        return null;
    }

    private Optional<Message> processEmailMessage(String msg) {
        return processMessage(msg, emailMessagePattern, Channel.Email);
    }

    private Optional<Message> processSmsMessage(String msg) {
        return processMessage(msg, smsMessagePattern, Channel.Sms);
    }

    private Optional<Message> processMessage(String msg, final Pattern pattern, final Channel channel) {
        final Matcher m = pattern.matcher(msg);

        if (m.matches()) {

            final String address = m.group(1);
            final String message = m.group(2);

            return Optional.of(new Message("", "", address, message, mutableClockFactory.clock().instant(),
                    channel));
        }

        return Optional.empty();
    }

    private boolean processSetDateInstruction(final String msg) {
        final Matcher m = setDateMessagePattern.matcher(msg);

        if (m.matches()) {

            final String dateAsString = m.group(1);

            try {
                final Date parse = dateFormat.parse(dateAsString);
                mutableClockFactory.fixInstant(parse.toInstant());
                publishCurrentDate();
                teamCaptainManager.applyWorkflowForAllCompetitions();
            } catch (final ParseException e) {
                log.warn("Failed to parse date in setDate instruction : {}", dateAsString, e);
            }

            return true;
        }

        return false;
    }

    private boolean processGetDateInstruction(final String msg) {

        if (getDateMessagePattern.matcher(msg).matches()) {
            publishCurrentDate();
            return true;
        }

        return false;
    }

    private void publishCurrentDate() {
        try {
            publishToVertxChat(createJsonMessage("Now is " + mutableClockFactory.clock().instant(),
                    "Team Captain System"));
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Failed generated vertx json message for getDate response", e);
        }
    }

    private void publishToVertxChat(final JsonNode node) throws JsonProcessingException {
        final String msgAsText = new ObjectMapper().writeValueAsString(node);
        log.debug("Publishing outgoing message to vertx chat.room.competition : {} from node : {}", msgAsText, node);
        final Set<String> set = vertx.sharedData().getSet("chat.room.competition");
        set.stream().forEach(id -> eventBus.publish(id, msgAsText));
    }

    private JsonNode createJsonMessage(String body, String sourceIdentity) {
        final JsonNodeFactory instance = JsonNodeFactory.instance;
        final ObjectNode objectNode = instance.objectNode();

        objectNode
                .set("message", instance.textNode(body));
        objectNode
                .set("sender", instance.textNode(sourceIdentity));
        objectNode
                .set("received", instance.textNode(mutableClockFactory.clock().instant().toString()));

        return objectNode;
    }

}
