package org.rrabarg.teamcaptain.domain;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mutable, must be thread-safe
 */
public class WorkflowState {

    // very likely to change at runtime
    private volatile MatchStatus matchStatus = MatchStatus.InWindow;
    private final Map<String, PlayerState> playerState;

    // unlikely to change at runtime
    private volatile String travelDetails;

    public WorkflowState(MatchStatus matchStatus, Map<String, PlayerState> playerState, String travelDetails) {
        this.matchStatus = matchStatus;
        this.playerState = Collections.synchronizedMap(playerState);
    }

    public WorkflowState() {
        this(MatchStatus.InWindow, new HashMap<>(), null);
    }

    public void setMatchState(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public void setPlayerState(Player player, PlayerStatus playerStatus) {
        if (playerState.containsKey(player.getKey())) {
            playerState.get(player.getKey()).setPlayerStatus(playerStatus);
        } else {
            playerState.put(player.getKey(), new PlayerState(playerStatus));
        }
    }

    public MatchStatus getMatchState() {
        return matchStatus;
    }

    public PlayerStatus getPlayerStatus(Player player) {
        final PlayerState state = getPlayerState(player);
        return state == null ? PlayerStatus.None : state.getPlayerStatus();
    }

    public PlayerState getPlayerState(Player player) {
        return playerState.get(player.getKey());
    }

    public Map<String, PlayerStatus> getPlayerStates() {
        return playerState.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue().getPlayerStatus()));
    }

    public void setLastNotification(PlayerNotification notification) {
        final String key = notification.getPlayer().getKey();

        if (playerState.containsKey(key)) {
            playerState.get(key).setLastNotification(notification);
        } else {
            playerState.put(key, new PlayerState(PlayerStatus.None));
        }

    }

    public void setTravelDetails(String travelDetails) {
        this.travelDetails = travelDetails;
    }

    public String getTravelDetails() {
        return travelDetails;
    }

}