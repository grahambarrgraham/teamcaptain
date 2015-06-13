package org.rrabarg.teamcaptain.domain;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Collection;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface SelectionStrategy {

    Collection<Player> firstPick(PlayerPool pool);

    Player nextPick(PlayerPool pool, Player decline);

    boolean isViable(List<Player> acceptedPlayers);

}
