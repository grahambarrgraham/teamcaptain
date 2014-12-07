package org.rrabarg.teamcaptain.domain;

public final class PlayerResponse {
    private final Match match;
    private final Player player;
    private final Kind kind;
    private final String content;

    public PlayerResponse(Match match, Player player, Kind kind, String content) {
        this.match = match;
        this.player = player;
        this.kind = kind;
        this.content = content;
    }

    public enum Kind {
        ICanPlay, ICantPlay, Information, Alert
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

    public String getContent() {
        return content;
    };

}
