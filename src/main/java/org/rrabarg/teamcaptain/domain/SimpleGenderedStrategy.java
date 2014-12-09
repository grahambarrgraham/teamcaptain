package org.rrabarg.teamcaptain.domain;

import java.util.ArrayList;
import java.util.Collection;
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

    public SimpleGenderedStrategy(
            @JsonProperty("numberOfGents") int numberOfGents,
            @JsonProperty("numberOfLadies") int numberOfLadies,
            @JsonProperty("daysTillMatchForReminders") int daysTillMatchForReminders,
            @JsonProperty("daysTillMatchForNotifications") int daysTillMatchForNotifications) {
        this.numberOfGents = numberOfGents;
        this.numberOfLadies = numberOfLadies;
        this.daysTillMatchForReminders = daysTillMatchForReminders;
        this.daysTillMatchForNotifications = daysTillMatchForNotifications;
    }

    @Override
    public Collection<Player> firstPick(PoolOfPlayers pool) {

        final List<Player> result = new ArrayList<>();

        final Map<Gender, List<Player>> groupedByGender = groupedByGender(pool);

        result.addAll(gentFirstPick(groupedByGender, Gender.Male, numberOfGents));
        result.addAll(gentFirstPick(groupedByGender, Gender.Female, numberOfLadies));

        return result;
    }

    private List<Player> gentFirstPick(final Map<Gender, List<Player>> groupedByGender, Gender male, int max) {
        final List<Player> gents = groupedByGender.get(male);
        sortPlayers(gents);
        return gents.subList(0, max);
    }

    private void sortPlayers(final List<Player> gents) {
        // trivial alphabetic
        gents.sort((o1, o2) -> o1.getKey().compareTo(o2.getKey()));
    }

    private Map<Gender, List<Player>> groupedByGender(PoolOfPlayers pool) {
        final Map<Gender, List<Player>> groupedByGender = pool.getPlayers().stream()
                .collect(Collectors.groupingBy(a -> a.getGender()));
        return groupedByGender;
    }

    @Override
    public Player nextPick(PoolOfPlayers pool, Player decline) {
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

}
