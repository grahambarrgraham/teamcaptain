package org.rrabarg.teamcaptain.domain;

import java.util.HashMap;
import java.util.Map;

public class WorkflowState {

    private MatchState matchState = MatchState.InWindow;
    private Map<String, PlayerState> playerStates;

    public WorkflowState(MatchState matchState, Map<String, PlayerState> playerStates) {
        this.matchState = matchState;
        this.playerStates = playerStates;
    }

    public WorkflowState() {
        this(MatchState.InWindow, new HashMap<>());
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
        return playerStates;
    }

    public void setPlayerStates(Map<String, PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public void substitute(Player player, Player substitute, PlayerState state) {
        playerStates.put(substitute.getKey(), state);
        playerStates.remove(player.getKey());
    }

}