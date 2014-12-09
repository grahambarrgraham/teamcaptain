package org.rrabarg.teamcaptain;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;
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

    @Autowired
    Clock clock;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundEmail), this);
    }

    private final Map<String, Stack<Email>> notificationMap = new HashMap<>();

    @Override
    public synchronized void accept(Event<Email> event) {

        log.debug("Test mailbox intercepting outgoing email to " + event.getData().getToAddress());

        final Email email = event.getData();

        Stack<Email> stack = notificationMap.get(email.getToAddress());

        if (stack == null) {
            log.debug("Test mailbox adding stack for " + email.getToAddress());
            stack = new Stack<>();
            notificationMap.put(email.getToAddress(), stack);
        }

        stack.add(email);
    }

    public synchronized Email pop(String address) {
        return popper(address);
    }

    private Email popper(String address) {
        final Stack<Email> stack = notificationMap.get(address);
        final Email email = (stack == null) || stack.isEmpty() ? null : stack.pop();
        log.debug("Test mailbox check stack for " + address + ". Null stack : " + (stack == null) + " Null email : "
                + (email == null));
        return email;
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
        reactor.notify(ReactorMessageKind.InboundEmail, new Event<>(inboundEmail));
    }

}
