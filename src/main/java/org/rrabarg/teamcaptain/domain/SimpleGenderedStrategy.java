package org.rrabarg.teamcaptain.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rrabarg.teamcaptain.SelectionStrategy;

public class SimpleGenderedStrategy implements SelectionStrategy {
    private final int numberOfGents;
    private final int numberOfLadies;
    private final int daysTillMatchForReminders;
    private final int daysTillMatchForNotifications;
    private final int daysTillMatchForStandbys;

    public SimpleGenderedStrategy(
            @JsonProperty("numberOfGents") int numberOfGents,
            @JsonProperty("numberOfLadies") int numberOfLadies,
            @JsonProperty("daysTillMatchForReminders") int daysTillMatchForReminders,
            @JsonProperty("daysTillMatchForNotifications") int daysTillMatchForNotifications,
            @JsonProperty("daysTillMatchForStandbys") int daysTillMatchForStandbys) {
        this.numberOfGents = numberOfGents;
        this.numberOfLadies = numberOfLadies;
        this.daysTillMatchForReminders = daysTillMatchForReminders;
        this.daysTillMatchForNotifications = daysTillMatchForNotifications;
        this.daysTillMatchForStandbys = daysTillMatchForStandbys;
    }

    @Override
    public Collection<Player> firstPick(PlayerPool pool) {

        final List<Player> result = new ArrayList<>();

        final Map<Gender, List<Player>> groupedByGender = groupedByGender(pool);

        result.addAll(firstPick(groupedByGender, Gender.Male, numberOfGents));
        result.addAll(firstPick(groupedByGender, Gender.Female, numberOfLadies));

        return result;
    }

    private List<Player> firstPick(final Map<Gender, List<Player>> groupedByGender, Gender gender, int max) {
        final List<Player> players = groupedByGender.get(gender);

        if (players == null) {
            return Collections.emptyList();
        }

        sortPlayers(players);
        return players.subList(0, max);
    }

    private void sortPlayers(final List<Player> gents) {
        // trivial alphabetic
        gents.sort((o1, o2) -> o1.getKey().compareTo(o2.getKey()));
    }

    private Map<Gender, List<Player>> groupedByGender(PlayerPool pool) {
        final Map<Gender, List<Player>> groupedByGender = pool.getPlayers().stream()
                .collect(Collectors.groupingBy(a -> a.getGender()));
        return groupedByGender;
    }

    @Override
    public Player nextPick(PlayerPool pool, Player decline) {
        final List<Player> list = groupedByGender(pool).get(decline.getGender());
        sortPlayers(list);
        final int indexOfNextPlayer = list.indexOf(decline) + 1;
        return (indexOfNextPlayer) >= list.size() ? null : list.get(indexOfNextPlayer);
    }

    public int getNumberOfGents() {
        return numberOfGents;
    }

    public int getNumberOfLadies() {
        return numberOfLadies;
    }

    @Override
    public int getDaysTillMatchForReminders() {
        return daysTillMatchForReminders;
    }

    @Override
    public int getDaysTillMatchForNotifications() {
        return daysTillMatchForNotifications;
    }

    @Override
    public long getDaysTillMatchForStandbys() {
        return daysTillMatchForStandbys;
    }

    @Override
    public boolean isViable(List<Player> acceptedPlayers) {

        final Map<Gender, List<Player>> collect = acceptedPlayers.stream().collect(
                Collectors.groupingBy(p -> p.getGender()));

        return ((getNumberOfPlayersOfGender(collect, Gender.Female) == numberOfLadies) && (getNumberOfPlayersOfGender(
                collect, Gender.Male) == numberOfGents));

    }

    public int getNumberOfPlayersOfGender(final Map<Gender, List<Player>> collect, Gender female) {
        final int size = collect.get(female) == null ? 0 : collect.get(female).size();
        return size;
    }
}
