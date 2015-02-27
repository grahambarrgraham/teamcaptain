package org.rrabarg.teamcaptain.channel.sms;

import static reactor.event.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.channel.NotificationMatcherService;
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
public class SmsInboundService implements Consumer<Event<SmsMessage>> {

    static Logger log = LoggerFactory.getLogger(SmsInboundService.class);

    @Autowired
    private Reactor reactor;

    @Autowired
    private NotificationMatcherService smsNotificationMatcherService;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.InboundSms), this);
    }

    @Override
    public void accept(Event<SmsMessage> event) {

        try {

            log.debug("Receive SMS from " + event.getData().getPhone());

            final PlayerResponse match = smsNotificationMatcherService.getMatch(event.getData());

            if (match != null) {
                log.debug("Matched it to : " + match.getKind());
                reactor.notify(ReactorMessageKind.InboundPlayerResponse, new Event<>(match));
            } else {
                // do something sensible, e.g. call the events error consumer, or notify on an unmatched email channel
                log.warn("Unmatched incoming SMS from " + event.getData().getPhone() + " with message "
                        + event.getData().getText());
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
