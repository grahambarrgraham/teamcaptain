package org.rrabarg.teamcaptain.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;
import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.SelectionStrategy;

public class CompetitionState {

    String playerPoolId;
    NotificationStrategy notificationStrategy;
    SelectionStrategy selectionStrategy;
    String teamCaptainId;

    public CompetitionState(
            @JsonProperty("playerPoolId") String playerPoolId,
            @JsonProperty("teamCaptainId") String teamCaptainId,
            @JsonProperty("selectionStrategy") SelectionStrategy selectionStrategy,
            @JsonProperty("notificationStrategy") NotificationStrategy notificationStrategy) {
        this.playerPoolId = playerPoolId;
        this.teamCaptainId = teamCaptainId;
        this.selectionStrategy = selectionStrategy;
        this.notificationStrategy = notificationStrategy;
    }

    public String getPlayerPoolId() {
        return playerPoolId;
    }

    public void setPlayerPoolId(String playerPoolId) {
        this.playerPoolId = playerPoolId;
    }

    public NotificationStrategy getNotificationStrategy() {
        return notificationStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    public void setNotificationStrategy(NotificationStrategy notificationStrategy) {
        this.notificationStrategy = notificationStrategy;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String getTeamCaptainId() {
        return getTeamCaptainId();
    }
}