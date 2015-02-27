package org.rrabarg.teamcaptain.channel.email;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.TeamCaptainNotification;
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
public class EmailOutboundService implements Consumer<Event<Notification>> {

    static Logger log = LoggerFactory.getLogger(EmailOutboundService.class);

    @Autowired
    Reactor reactor;

    @Autowired
    EmailPlayerNotificationRenderer playerNotificationRenderer;

    @Autowired
    EmailAdminAlertRenderer adminAlertRenderer;

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

        if (notification instanceof TeamCaptainNotification) {
            reactor.notify(ReactorMessageKind.OutboundEmail,
                    new Event<>(adminAlertRenderer.render((TeamCaptainNotification) notification)));
        } else {
            reactor.notify(ReactorMessageKind.OutboundEmail,
                    new Event<>(playerNotificationRenderer.render((PlayerNotification) notification)));
        }
    }

}
