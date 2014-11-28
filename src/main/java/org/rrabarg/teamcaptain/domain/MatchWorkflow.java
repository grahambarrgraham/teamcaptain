package org.rrabarg.teamcaptain.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchWorkflow {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private final Match match;
    private final Competition competition;

    public MatchWorkflow(Competition comp, Match match) {
        competition = comp;
        this.match = match;
    }

    public MatchWorkflow event() {
        competition.getPlayerPool().getPlayers().stream()
                .peek(player -> log.debug("matchUpcoming event for " + player + " for " + match))
                .forEach(player -> event(player));
        return this;
    }

    private void event(Player player) {
        // TODO implement this
    }

}
