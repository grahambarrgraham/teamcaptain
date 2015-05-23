package org.rrabarg.teamcaptain.domain;

import static org.rrabarg.teamcaptain.domain.Notification.Category.Information;
import static org.rrabarg.teamcaptain.domain.Notification.Category.Question;

import org.rrabarg.teamcaptain.domain.Notification.Category;

public enum NotificationKind {

    CanYouPlay(Question),
    Reminder(Question),
    StandBy(Question),
    StandDown(Question),

    ConfirmationOfAcceptance(Information),
    ConfirmationOfDecline(Information),
    ConfirmationOfStandby(Information),
    MatchConfirmation(Information),

    StandbyPlayersNotified(Information),
    MatchFulfilled(Information),
    InsufficientPlayers(Information),
    OutOfBandMessage(Information),
    MatchStatusUpdate(Information);

    private final Category category;

    NotificationKind(Category category) {
        this.category = category;
    }

    public boolean expectsResponse() {
        return getCategory() == Question;
    }

    public Category getCategory() {
        return category;
    }
}