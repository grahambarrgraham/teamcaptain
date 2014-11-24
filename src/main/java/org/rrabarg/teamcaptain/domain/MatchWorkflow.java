package org.rrabarg.teamcaptain.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchWorkflow {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private Match match;
    private String state;

    public MatchWorkflow() {
    }

    public MatchWorkflow(String state) {
        this.state = state;
    }

    public MatchWorkflow matchUpcoming() {
        return this;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

}
