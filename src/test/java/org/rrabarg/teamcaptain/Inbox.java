package org.rrabarg.teamcaptain;

import static reactor.event.selector.Selectors.$;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.service.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class Inbox implements Consumer<Event<EmailNotification>> {

    static Logger log = LoggerFactory.getLogger(Inbox.class);

    @Autowired
    Reactor reactor;

    @PostConstruct
    public void configure() {
        reactor.on($("email"), this);
    }

    private final Map<String, Stack<EmailNotification>> notificationMap = new HashMap<>();

    @Override
    public void accept(Event<EmailNotification> event) {

        log.debug("Inbox received event " + event);

        final EmailNotification data = event.getData();

        Stack<EmailNotification> stack = notificationMap.get(data.getAddress());

        if (stack == null) {
            stack = new Stack<>();
            notificationMap.put(data.getAddress(), stack);
        }

        stack.add(data);
    }

    public EmailNotification pop(String address) {
        final Stack<EmailNotification> stack = notificationMap.get(address);
        return stack == null ? null : stack.pop();
    }

    public void clear() {
        notificationMap.clear();
    }

}
