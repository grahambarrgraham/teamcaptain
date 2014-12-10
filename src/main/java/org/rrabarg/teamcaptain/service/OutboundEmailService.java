package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.AdminAlert;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class OutboundEmailService implements Consumer<Event<Notification>> {

    static Logger log = LoggerFactory.getLogger(OutboundEmailService.class);

    @Autowired
    Reactor reactor;

    @Autowired
    PlayerNotificationRenderer playerNotificationRenderer;

    @Autowired
    AdminAlertRenderer adminAlertRenderer;

    @Autowired
    Provider<Clock> clock;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundPlayerNotification), this);
        reactor.on($(ReactorMessageKind.OutboundAdminAlert), this);
    }

    @Override
    public void accept(Event<Notification> event) {
        final Notification notification = event.getData();

        log.debug("Sending email : " + notification);

        if (notification instanceof AdminAlert) {
            reactor.notify(ReactorMessageKind.OutboundEmail,
                    new Event<>(adminAlertRenderer.render((AdminAlert) notification)));
        } else {
            reactor.notify(ReactorMessageKind.OutboundEmail,
                    new Event<>(playerNotificationRenderer.render((PlayerNotification) notification)));
        }
    }

}
