package org.rrabarg.teamcaptain.service.google;

import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.CompetitionState;
import org.rrabarg.teamcaptain.domain.PlayerPool;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.CompetitionStateService;
import org.rrabarg.teamcaptain.service.PlayerPoolService;
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompetitionGoogleService implements CompetitionService {

    Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    CompetitionStateService competitionStateRepository;

    @Autowired
    PlayerPoolService playerPoolService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rrabarg.teamcaptain.service.CompetitionService#saveCompetition(org.rrabarg.teamcaptain.domain.Competition)
     */
    @Override
    public String saveCompetition(Competition competition) {
        try {
            final String name = competition.getName();
            final String poolId = playerPoolService.savePlayerPool(name, competition.getPlayerPool());
            final String scheduleId = scheduleService.saveSchedule(name, competition.getSchedule());
            competitionStateRepository.save(null, new CompetitionState(poolId, competition.getSelectionStrategy()));
            return scheduleId;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to save competition " + competition, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rrabarg.teamcaptain.service.CompetitionService#findCompetitionByName(java.lang.String)
     */
    @Override
    public Competition findCompetitionByName(String competitionName) {

        try {
            final Schedule schedule = scheduleService.findByName(competitionName);

            if (schedule == null) {
                return null;
            }

            final CompetitionState competitionState = competitionStateRepository.getCompetitionState(competitionName);

            schedule.getMatches().stream().map(a -> a.getWorkflowState())
                    .forEach(a -> log.debug("Schedule " + schedule.getId() + " loaded match state " + a));

            final PlayerPool playerPool = findPlayerPool(competitionName, competitionState.getPlayerPoolId());

            final Competition competition = new Competition(competitionName, schedule, playerPool,
                    competitionState.getSelectionStrategy());

            schedule.setCompetition(competition);
            competition.setId(competitionName);

            return competition;

        } catch (final Exception e) {
            throw new RuntimeException("Failure whilst try to find competition with name " + competitionName, e);
        }
    }

    private PlayerPool findPlayerPool(String competitionName, String playerPoolId) {

        final PlayerPool pool = playerPoolService.findById(playerPoolId);

        if (pool == null) {
            throw new RuntimeException("Competition " + competitionName
                    + " in invalid state, could not find player pool " + playerPoolId);
        }
        return pool;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rrabarg.teamcaptain.service.CompetitionService#clearCompetition(org.rrabarg.teamcaptain.domain.Competition)
     */
    @Override
    public void clearCompetition(Competition competition) {
        try {
            scheduleService.clearMatches(competition.getSchedule());
            playerPoolService.clearPlayers(competition.getName(), competition.getPlayerPool());
        } catch (final Exception e) {
            throw new RuntimeException("Failed to clear competition " + competition.getName(), e);
        }
    }

}
