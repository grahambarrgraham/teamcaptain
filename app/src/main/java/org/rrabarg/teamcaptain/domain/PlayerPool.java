package org.rrabarg.teamcaptain.domain;

import org.rrabarg.teamcaptain.util.Util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

//@Entity
public class PlayerPool {

    //@Id
    private String id;

    protected final Map<String, Player> players;

    private final TeamCaptain teamCaptain;

    /**
     * Used for entity creation
     */
    public PlayerPool(TeamCaptain teamCaptain, Player... players) {
        this.teamCaptain = teamCaptain;
        this.players = createMap(Arrays.asList(players));
    }

    /**
     * Used for load from persistent store
     */
    public PlayerPool(String poolId, TeamCaptain teamCaptain, Collection<Player> players) {
        this.teamCaptain = teamCaptain;
        this.players = createMap(players);
        this.id = poolId;
    }

    public TeamCaptain getTeamCaptain() {
        return teamCaptain;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pool " + players;
    }

    public Player getPlayerForKey(String k) {
        return players.get(k);
    }

    private Map<String, Player> createMap(Collection<Player> players) {
        final Map<String, Player> result =
                players.stream().collect(Util.toLinkedMap(Player::getKey,
                        Function.<Player> identity()));
        return result;
    }

}
