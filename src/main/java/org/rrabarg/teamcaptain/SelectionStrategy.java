package org.rrabarg.teamcaptain;

import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerPool;

// @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
// @JsonSubTypes({@Type(value = Cat.class, name = "cat"),@Type(value = Dog.class, name = "dog") })

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface SelectionStrategy {

    Collection<Player> firstPick(PlayerPool pool);

    Player nextPick(PlayerPool pool, Player decline);

    boolean isViable(List<Player> acceptedPlayers);

    int getDaysTillMatchForReminders();

    int getDaysTillMatchForNotifications();

    long getDaysTillMatchForStandbys();

}
