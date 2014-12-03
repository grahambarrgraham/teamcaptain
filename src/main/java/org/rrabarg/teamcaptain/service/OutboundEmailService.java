package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class OutboundEmailService implements Consumer<Event<Email>> {

    static Logger log = LoggerFactory.getLogger(OutboundEmailService.class);

    @Autowired
    Reactor reactor;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundEmail.toString()), this);
    }

    @Override
    public void accept(Event<Email> event) {
        log.info("To be implemented!!! Email service received event " + event);
    }

}
