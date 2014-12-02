package org.rrabarg.teamcaptain.service;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerNotification.Kind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.Reactor;
import reactor.event.Event;

@Component
public class NotificationService {

    @Autowired
    EmailNotificationRenderer renderer;

    @Autowired
    Reactor reactor;

    public void notify(Match match, Player player, Kind kind) {
        reactor.notify("email", new Event<>(renderer.render(new PlayerNotification(match, player, kind))));
    }
}
