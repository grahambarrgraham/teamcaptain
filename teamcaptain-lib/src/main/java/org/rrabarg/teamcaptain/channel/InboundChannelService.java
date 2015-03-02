package org.rrabarg.teamcaptain.channel;

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
public class InboundChannelService implements Consumer<Event<Message>> {

    static Logger log = LoggerFactory.getLogger(InboundChannelService.class);

    @Autowired
    private Reactor reactor;

    @Autowired
    private NotificationMatcherService notificationMatcherService;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.InboundChannelMessage), this);
    }

    @Override
    public void accept(Event<Message> event) {

        final Message message = event.getData();

        try {

            log.debug("Receive " + message.getChannel() + " from " + message.getSourceIdentity());

            final PlayerResponse match = notificationMatcherService.getMatch(message);

            if (match != null) {

                log.debug("Matched it to : " + match.getKind());

                reactor.notify(ReactorMessageKind.InboundNotification, new Event<>(match));
            } else {
                log.warn("Unmatched incoming message " + message);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
