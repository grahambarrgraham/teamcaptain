package org.rrabarg.teamcaptain.domain;

import java.time.ZonedDateTime;

import javax.persistence.Entity;

@Entity
public class Match {

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

    public PlayerState getPlayerState(Player player) {
        return workflowState.getPlayerState(player);
    }

    public MatchState getMatchState() {
        return workflowState.getMatchState();
    }

    public void setMatchState(MatchState matchState) {
        workflowState.setMatchState(matchState);
    }

    public void setPlayerState(Player player, PlayerState playerState) {
        workflowState.setPlayerState(player, playerState);
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

}
