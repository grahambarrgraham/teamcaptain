package org.rrabarg.teamcaptain;

import static reactor.event.selector.Selectors.$;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.service.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class TestMailbox implements Consumer<Event<Email>> {

    static Logger log = LoggerFactory.getLogger(TestMailbox.class);

    @Autowired
    Reactor reactor;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundEmail), this);
    }

    private final Map<String, Stack<Email>> notificationMap = new HashMap<>();

    @Override
    public void accept(Event<Email> event) {

        log.debug("Inbox received event " + event);

        final Email data = event.getData();

        Stack<Email> stack = notificationMap.get(data.getAddress());

        if (stack == null) {
            stack = new Stack<>();
            notificationMap.put(data.getAddress(), stack);
        }

        stack.add(data);
    }

    public Email pop(String address) {
        final Stack<Email> stack = notificationMap.get(address);
        return stack == null ? null : stack.pop();
    }

    public void clear() {
        notificationMap.clear();
    }

    public TestEmailBuilder email() {
        return new TestEmailBuilder();
    }

    public class TestEmailBuilder {
        private String subject;
        private String fromAddress;
        private String body;

        public TestEmailBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public TestEmailBuilder from(String from) {
            this.fromAddress = from;
            return this;
        }

        public TestEmailBuilder body(String body) {
            this.body = body;
            return this;
        }

        public Email build() {
            return new Email(subject, null, fromAddress, body);
        }

        public void send() {
            TestMailbox.this.send(build());
        }
    }

    public void send(Email inboundEmail) {
        reactor.notify(ReactorMessageKind.InboundEmail, new Event<>(inboundEmail));
    }

}
