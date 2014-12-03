package org.rrabarg.teamcaptain.domain;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

import org.rrabarg.teamcaptain.workflow.Definition.MatchState;
import org.rrabarg.teamcaptain.workflow.Definition.PlayerState;

@Entity
public class Match {

    private final String title;
    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
    private final Location location;

    private MatchState matchState = MatchState.InWindow;
    private final Map<String, PlayerState> playerStates = new HashMap<>();

    public Match(String title, ZonedDateTime startDateTime, ZonedDateTime endDateTime, Location location) {
        this(null, title, startDateTime, endDateTime, location);
    }

    public Match(String id, String title, ZonedDateTime startDateTime, ZonedDateTime endDateTime, Location location) {
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    @Override
    public String toString() {
        return title;
    }

    public MatchState getState() {
        return matchState;
    }

    public PlayerState getPlayerState(Player joe) {
        return null;
    }

    public void setMatchState(MatchState matchState) {
        this.matchState = matchState;
    }

    public void setPlayerState(Player player, PlayerState playerState) {
        playerStates.put(player.getKey(), playerState);
    }
}
