package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerNotification.Kind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class OutboundEmailService implements Consumer<Event<PlayerNotification>> {

    static Logger log = LoggerFactory.getLogger(OutboundEmailService.class);

    @Autowired
    Reactor reactor;

    @Autowired
    EmailNotificationRenderer renderer;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundPlayerNotification), this);
    }

    @Override
    public void accept(Event<PlayerNotification> event) {
        final PlayerNotification notification = event.getData();
        notify(notification.getMatch(), notification.getPlayer(), notification.getKind());
    }

    public void notify(Match match, Player player, Kind kind) {

        final PlayerNotification notification = new PlayerNotification(match, player, kind);

        log.debug("Sending email : " + notification);

        reactor.notify(ReactorMessageKind.OutboundEmail,
                new Event<>(renderer.render(notification)));
    }

}
