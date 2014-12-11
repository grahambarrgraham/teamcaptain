package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class InboundEmailService implements Consumer<Event<Email>> {

    static Logger log = LoggerFactory.getLogger(InboundEmailService.class);

    @Autowired
    private Reactor reactor;

    @Autowired
    private NotificationMatcherService matcherService;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.InboundEmail), this);
    }

    @Override
    public void accept(Event<Email> event) {

        try {

            log.debug("Receive email from " + event.getData().getFromAddress());

            final PlayerResponse match = matcherService.getMatch(event.getData());

            if (match != null) {
                log.debug("Matched it to : " + match.getKind());
                reactor.notify(ReactorMessageKind.InboundPlayerResponse, new Event<>(match));
            } else {
                // do something sensible, e.g. call the events error consumer, or notify on an unmatched email channel
                log.warn("Unmatched incoming email from " + event.getData().getFromAddress() + " with subject "
                        + event.getData().getSubject());
            }
        } catch (final Exception e) {
            log.error("wow", e);
            throw new RuntimeException(e);
        }
    }

}
