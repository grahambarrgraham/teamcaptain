package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;
import java.util.Collection;

import org.rrabarg.teamcaptain.domain.*;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.PlayerPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("google")
public class CompetitionGoogleService implements CompetitionService {

    @Autowired
    ScheduleGoogleService scheduleService;

    @Autowired
    CompetitionStateGoogleService competitionStateService;

    @Autowired
    PlayerPoolService playerPoolService;

    @Override
    public String createCompetition(Competition competition) {
        try {
            final String name = competition.getName();
            final String poolId = playerPoolService.savePlayerPool(name, competition.getPlayerPool());
            final String scheduleId = scheduleService.saveSchedule(name, competition.getSchedule());

            competition.setId(scheduleId);

            competitionStateService.save(
                    scheduleId,
                    new CompetitionState(poolId,
                            competition.getSelectionStrategy(),
                            competition.getNotificationStrategy()));

            return scheduleId;

        } catch (final Exception e) {
            throw new RuntimeException("Failed to save competition " + competition, e);
        }
    }

    @Override
    public Competition findCompetitionById(String competitionId) {

        try {
            final Schedule schedule = scheduleService.getScheduleById(competitionId);

            if (schedule == null) {
                return null;
            }

            final CompetitionState competitionState = competitionStateService.getCompetitionState(competitionId);

            final PlayerPool playerPool = findPlayerPool(competitionState.getPlayerPoolId());

            final Competition competition = new Competition(competitionId, schedule, playerPool,
                    competitionState.getSelectionStrategy(),
                    competitionState.getNotificationStrategy());

            schedule.setCompetition(competition);
            competition.setId(competitionId);

            return competition;

        } catch (final Exception e) {
            throw new RuntimeException("Failure whilst try to find competition with name " + competitionId, e);
        }
    }

    @Override
    public Competition getCompetitionByName(String competitionName) throws IOException {
        return findCompetitionById(scheduleService.getScheduleIdForName(competitionName));
    }

    @Override
    public void updateMatch(Match match) throws IOException {
        scheduleService.updateMatch(match);
    }

    @Override
    public void clearCompetition(Competition comp) {
        throw new UnsupportedOperationException("Clear competition not implemented");
    }

    private PlayerPool findPlayerPool(String playerPoolId) {

        final PlayerPool pool = playerPoolService.findById(playerPoolId);

        if (pool == null) {
            throw new RuntimeException("could not find player pool " + playerPoolId);
        }
        return pool;
    }

    @Override
    public Collection<String> getCompetitionIds() {
        try {
            return scheduleService.getAllScheduleIds();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get all competition ids", e);
        }
    }

}
