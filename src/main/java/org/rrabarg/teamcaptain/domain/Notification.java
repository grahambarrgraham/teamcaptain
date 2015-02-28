package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

import org.rrabarg.teamcaptain.strategy.ContactPreference;

public abstract class Notification {

    protected final Match match;
    protected final Instant timestamp;
    protected final Competition competition;
    private final NotificationKind kind;

    public enum Category {
        Information, Question
    };

    public Notification(Competition competition, Instant timestamp, Match match, NotificationKind kind) {
        this.competition = competition;
        this.timestamp = timestamp;
        this.match = match;
        this.kind = kind;
    }

    public Match getMatch() {
        return match;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PlayerPool getPlayerPool() {
        return competition.getPlayerPool();
    }

    public TeamCaptain getTeamCaptain() {
        return competition.getTeamCaptain();
    }

    public ContactPreference getCompetitionContactPreference() {
        return competition.getNotificationStrategy().getContactPreference();
    }

    public ContactPreference getTargetContactPreference() {
        return getTarget().getContactPreference();
    }

    public ContactDetail getTargetContactDetail() {
        return getTarget().getContactDetail();
    }

    public NotificationKind getKind() {
        return kind;
    }

    public Category getCategory() {
        return getKind().getCategory();
    }

    public abstract User getTarget();

    @Override
    public String toString() {
        return getTarget().getKey() + " : " + kind + " for " + match.getTitle() + " with timestamp " + timestamp;
    }

}
