package org.rrabarg.teamcaptain.workflow;

public class Definition {

    enum MatchState {
        InWindow, FirstPickPlayersNotified, Substitutions, MatchFulfilled, UnfulfilledAlert, DetailsPublished, MatchOutOfWindow
    };

    enum PlayerState {
        InWindow, Notified, Accepted, Declined, FailedToRespond, OnStandby, StoodDown, Confirmed, OutOfWindow
    };

    enum MatchEvents {
        EnterWindow, NotificationsSent, Fulfilled, DetailsPublished, OutOfWindow
    };

}
