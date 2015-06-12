package org.rrabarg.teamcaptain.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Match {

    @Id
    private String id;

    private String scheduleId;

    private final String title;
    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
    private final Location location;
    private final WorkflowState workflowState;

    /**
     * Constructor used for inception
     */
    public Match(String title, ZonedDateTime startDateTime, ZonedDateTime endDateTime, Location location) {
        this(null, null, title, startDateTime, endDateTime, location, null);
    }

    /**
     * Constructor used for load from persistent store
     */
    public Match(String id, String scheduleId, String title, ZonedDateTime startDateTime, ZonedDateTime endDateTime,
            Location location, WorkflowState workflowState) {
        this.id = id;
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.workflowState = workflowState == null ? new WorkflowState() : workflowState;
        this.scheduleId = scheduleId;
    }

    /**
     * Used by save to persistent store
     */
    public void init(String id, String scheduleId) {
        this.scheduleId = scheduleId;
        this.id = id;
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

    public PlayerStatus getPlayerState(Player player) {
        return workflowState.getPlayerStatus(player);
    }

    public MatchStatus getMatchStatus() {
        return workflowState.getMatchState();
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        workflowState.setMatchState(matchStatus);
    }

    public void setPlayerStatus(Player player, PlayerStatus playerStatus) {
        workflowState.setPlayerState(player, playerStatus);
    }

    public void setTravelDetails(String travelDetails) {
        workflowState.setTravelDetails(travelDetails);
    }

    public WorkflowState getWorkflowState() {
        return workflowState;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Match other = (Match) obj;
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        return true;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getId() {
        return id;
    }

    public String getTravelDetails() {
        return workflowState.getTravelDetails();
    }

    public List<Player> getAcceptedPlayers(PlayerPool pool) {
        return getPlayersInState(PlayerStatus.Accepted, pool);
    }

    public List<Player> getConfirmedPlayers(PlayerPool pool) {
        return getPlayersInState(PlayerStatus.Confirmed, pool);
    }

    public List<Player> getDeclinedPlayers(PlayerPool playerPool) {
        return getPlayersInState(PlayerStatus.Declined, playerPool);
    }

    public List<Player> getNotifiedPlayers(PlayerPool playerPool) {
        return getPlayersInState(PlayerStatus.Notified, playerPool);
    }

    public List<Player> getNotifiedForStandbyPlayers(PlayerPool playerPool) {
        return getPlayersInState(PlayerStatus.NotifiedForStandby, playerPool);
    }

    public List<Player> getAcceptedOnStandbyPlayers(PlayerPool playerPool) {
        return getPlayersInState(PlayerStatus.AcceptedOnStandby, playerPool);
    }

    private List<Player> getPlayersInState(PlayerStatus playerStatus, PlayerPool pool) {
        final List<Player> playerKeys = workflowState.getPlayerStates().entrySet().stream()
                .filter(e -> e.getValue() == playerStatus)
                .map(e -> e.getKey()).map(k -> pool.getPlayerForKey(k)).collect(Collectors.toList());
        return playerKeys;
    }
}