package org.rrabarg.teamcaptain.workflow;

public class Definition {

    public enum MatchState {
        InWindow, FirstPickPlayersNotified, Substitutions, MatchFulfilled, UnfulfilledAlert, DetailsPublished, MatchOutOfWindow
    };

    public enum PlayerState {
        InWindow, Notified, Accepted, Declined, FailedToRespond, OnStandby, StoodDown, Confirmed, OutOfWindow
    };

    enum MatchEvents {
        EnterWindow, NotificationsSent, Fulfilled, DetailsPublished, OutOfWindow
    };

}
