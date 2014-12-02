package org.rrabarg.teamcaptain.domain;

public final class PlayerNotification {
    private final Match match;
    private final Player player;
    private final Kind kind;

    public PlayerNotification(Match match, Player player, Kind kind) {
        this.match = match;
        this.player = player;
        this.kind = kind;
    }

    public enum Kind {
        CanYouPlay, Reminder, StandBy, StandDown, Confirmation
    }

    public Match getMatch() {
        return match;
    }

    public Player getPlayer() {
        return player;
    }

    public Kind getKind() {
        return kind;
    }

    public String getOrganiserFirstName() {
        return "Graham";
    };

}
