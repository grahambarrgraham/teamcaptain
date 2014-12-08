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

    public SimpleGenderedStrategy(
            @JsonProperty("numberOfGents") int numberOfGents,
            @JsonProperty("numberOfLadies") int numberOfLadies) {
        this.numberOfGents = numberOfGents;
        this.numberOfLadies = numberOfLadies;
    }

    @Override
    public Collection<Player> firstPick(PoolOfPlayers pool) {

        final List<Player> result = new ArrayList<>();

        final Map<Gender, List<Player>> groupedByGender = groupedByGender(pool);

        result.addAll(groupedByGender.get(Gender.Male).subList(0, numberOfGents));
        result.addAll(groupedByGender.get(Gender.Female).subList(0, numberOfLadies));

        return result;
    }

    private Map<Gender, List<Player>> groupedByGender(PoolOfPlayers pool) {
        final Map<Gender, List<Player>> groupedByGender = pool.getPlayers().stream()
                .collect(Collectors.groupingBy(a -> a.getGender()));
        return groupedByGender;
    }

    @Override
    public Player nextPick(PoolOfPlayers pool, Player decline) {
        final List<Player> list = groupedByGender(pool).get(decline.getGender());
        final int indexOfNextPlayer = list.indexOf(decline) + 1;
        return (indexOfNextPlayer) >= list.size() ? null : list.get(indexOfNextPlayer);
    }

    public int getNumberOfGents() {
        return numberOfGents;
    }

    public int getNumberOfLadies() {
        return numberOfLadies;
    }

}
