package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class EmailService implements Consumer<Event<EmailNotification>> {

    static Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    Reactor reactor;

    @PostConstruct
    public void configure() {
        reactor.on($("email"), this);
    }

    @Override
    public void accept(Event<EmailNotification> event) {
        log.info("To be implemented!!! Email service received event " + event);
    }

}
