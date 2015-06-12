package org.rrabarg.teamcaptain;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.channel.Email;
import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.domain.ReactorMessageKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class TestMailbox implements Consumer<Event<Message>> {

    static Logger log = LoggerFactory.getLogger(TestMailbox.class);

    @Autowired
    Reactor reactor;

    @Autowired
    Clock clock;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundEmail), this);
    }

    private final Map<String, Stack<Message>> notificationMap = new HashMap<>();

    @Override
    public synchronized void accept(Event<Message> event) {

        // log.debug("Test mailbox intercepting outgoing email to " + event.getData().getToAddress());

        final Message email = event.getData();

        Stack<Message> stack = notificationMap.get(email.getToAddress());

        if (stack == null) {
            // log.debug("Test mailbox adding stack for " + email.getToAddress());
            stack = new Stack<>();
            notificationMap.put(email.getToAddress(), stack);
        }

        stack.add(email);
    }

    public synchronized Message pop(String address) {
        final Stack<Message> stack = notificationMap.get(address);
        return (stack == null) || stack.isEmpty() ? null : stack.pop();
    }

    public synchronized Stream<Message> viewAll(String address) {
        final Stack<Message> stack = notificationMap.get(address);
        return (stack == null) || stack.isEmpty() ? Stream.empty() : stack.stream();
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
        private String toAddress;
        private String body;

        public TestEmailBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public TestEmailBuilder from(String from) {
            this.fromAddress = from;
            return this;
        }

        public TestEmailBuilder to(String to) {
            this.toAddress = to;
            return this;
        }

        public TestEmailBuilder body(String body) {
            this.body = body;
            return this;
        }

        public Email build() {
            return new Email(subject, toAddress, fromAddress, body, clock.instant());
        }

        public void send() {
            TestMailbox.this.send(build());
        }
    }

    public void send(Email inboundEmail) {
        reactor.notify(ReactorMessageKind.InboundChannelMessage, new Event<>(inboundEmail));
    }

    public Optional<Message> peek(String emailAddress) {
        final Stack<Message> stack = notificationMap.get(emailAddress);
        return Optional.ofNullable((stack == null) || stack.isEmpty() ? null : stack.peek());
    }

}
