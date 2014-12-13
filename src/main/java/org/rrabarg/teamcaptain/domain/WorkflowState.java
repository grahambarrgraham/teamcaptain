package org.rrabarg.teamcaptain.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mutable, must be thread-safe
 */
public class WorkflowState {

    // very likely to change at runtime
    private volatile MatchState matchState = MatchState.InWindow;
    private Map<String, PlayerState> playerStates;

    // unlikely to change at runtime
    private volatile String travelDetails;

    public WorkflowState(MatchState matchState, Map<String, PlayerState> playerStates, String travelDetails) {
        this.matchState = matchState;
        this.playerStates = Collections.synchronizedMap(playerStates);
    }

    public WorkflowState() {
        this(MatchState.InWindow, new HashMap<>(), null);
    }

    public void setMatchState(MatchState matchState) {
        this.matchState = matchState;
    }

    public void setPlayerState(Player player, PlayerState playerState) {
        playerStates.put(player.getKey(), playerState);
    }

    public MatchState getMatchState() {
        return matchState;
    }

    public PlayerState getPlayerState(Player player) {
        return playerStates.get(player.getKey());
    }

    public Map<String, PlayerState> getPlayerStates() {
        return new HashMap<String, PlayerState>(playerStates);
    }

    public void setPlayerStates(Map<String, PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public void setTravelDetails(String travelDetails) {
        this.travelDetails = travelDetails;
    }

    public void substitute(Player player, Player substitute, PlayerState state) {
        synchronized (playerStates) {
            playerStates.put(substitute.getKey(), state);
            playerStates.remove(player.getKey());
        }
    }

    public String getTravelDetails() {
        return travelDetails;
    }

}