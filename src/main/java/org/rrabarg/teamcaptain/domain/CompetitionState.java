package org.rrabarg.teamcaptain.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rrabarg.teamcaptain.SelectionStrategy;

public class CompetitionState {

    String playerPoolId;
    SelectionStrategy selectionStrategy;

    public CompetitionState(
            @JsonProperty("playerPoolId") String playerPoolId,
            @JsonProperty("selectionStrategy") SelectionStrategy selectionStrategy) {
        this.playerPoolId = playerPoolId;
        this.selectionStrategy = selectionStrategy;
    }

    public String getPlayerPoolId() {
        return playerPoolId;
    }

    public void setPlayerPoolId(String playerPoolId) {
        this.playerPoolId = playerPoolId;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

}