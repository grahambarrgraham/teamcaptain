package org.rrabarg.teamcaptain.domain;

import org.rrabarg.teamcaptain.domain.PlayerNotification.Kind;
import org.rrabarg.teamcaptain.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class MatchWorkflow {

    private static Logger log = LoggerFactory.getLogger(MatchWorkflow.class);

    @Autowired
    NotificationService notificationService;

    private Match match;
    private Competition competition;

    public MatchWorkflow canYouPlay() {

        competition
                .getPlayerPool()
                .getPlayers()
                .stream()
                .peek(player -> log.debug("notifying " + player + " for " + match + " for " + Kind.CanYouPlay))
                .forEach(
                        player -> notificationService.notify(match, player, Kind.CanYouPlay));
        return this;

    }

    @Required
    public void setup(Competition comp, Match match) {
        competition = comp;
        this.match = match;
    }
}