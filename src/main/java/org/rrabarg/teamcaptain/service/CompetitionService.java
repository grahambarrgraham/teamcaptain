package org.rrabarg.teamcaptain.service;

import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.CompetitionState;
import org.rrabarg.teamcaptain.domain.PoolOfPlayers;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompetitionService {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PlayerPoolService playerPoolService;

    public String saveCompetition(Competition competition) {
        try {
            final String name = competition.getName();
            final String poolId = playerPoolService.savePlayerPool(name, competition.getPlayerPool());
            return scheduleService.saveSchedule(name, competition.getSchedule(), poolId,
                    competition.getSelectionStrategy());
        } catch (final Exception e) {
            throw new RuntimeException("Failed to save competition " + competition, e);
        }
    }

    public Competition findCompetitionByName(String competitionName) {

        try {
            final Schedule schedule = scheduleService.findByName(competitionName);

            if (schedule == null) {
                return null;
            }

            final CompetitionState competitionState = schedule.getCompetitionState();

            return new Competition(competitionName, schedule, findPlayerPool(competitionName, competitionState),
                    competitionState.getSelectionStrategy());

        } catch (final Exception e) {
            throw new RuntimeException("Failure whilst try to find competition with name " + competitionName, e);
        }
    }

    private PoolOfPlayers findPlayerPool(String competitionName, final CompetitionState state) {
        final PoolOfPlayers pool = playerPoolService.findById(state.getPlayerPoolId());

        if (pool == null) {
            throw new RuntimeException("Competition " + competitionName
                    + " in invalid state, could not find player pool " + state.getPlayerPoolId());
        }
        return pool;
    }

    public void clearCompetition(Competition competition) {
        try {
            scheduleService.clearMatches(competition.getSchedule());
            playerPoolService.clearPlayers(competition.getPlayerPool());
        } catch (final Exception e) {
            throw new RuntimeException("Failed to clear competition " + competition.getName(), e);
        }
    }

}
