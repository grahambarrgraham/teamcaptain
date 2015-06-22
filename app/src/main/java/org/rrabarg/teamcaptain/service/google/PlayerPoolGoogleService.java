package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.PlayerPool;
import org.rrabarg.teamcaptain.service.PlayerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.gdata.util.ServiceException;

@Component
@Profile("google")
public class PlayerPoolGoogleService implements PlayerPoolService {

    Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    PlayerPoolGoogleRepository contactRepository;

    /*
     * (non-Javadoc)
     * 
     * @see org.rrabarg.teamcaptain.service.PlayerPoolService#savePlayerPool(java.lang.String,
     * org.rrabarg.teamcaptain.domain.PlayerPool)
     */
    @Override
    public String savePlayerPool(String name, final PlayerPool playerPool) {

        try {
            String poolId = playerPool.getId();

            if (poolId == null) {
                poolId = getOrCreatePlayerPoolId(name);
                playerPool.setId(poolId);
            } else {
                log.info("Save players to existing " + poolId);
            }

            log.debug("savePlayerPool, adding players " + playerPool.getPlayers());
            contactRepository.addPlayersToPool(poolId, playerPool.getPlayers());
            contactRepository.addTeamCaptainToPool(poolId, playerPool.getTeamCaptain());

            return poolId;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to save player pool " + playerPool + " for competition " + name, e);
        }
    }

    private String getOrCreatePlayerPoolId(String name) throws IOException, InterruptedException {

        try {
            String poolId = contactRepository.findPlayerPoolIdByName(name);
            if (poolId == null) {
                poolId = contactRepository.createPlayerPoolWithName(name);
                log.info("Created new pool for " + name + " with id " + poolId);
            } else {
                log.info("Found and reusing pool for " + name + " with id " + poolId);
            }

            return poolId;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to get or create player pool id for competition " + name, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rrabarg.teamcaptain.service.PlayerPoolService#findById(java.lang.String)
     */
    @Override
    public PlayerPool findById(String playerPoolId) {
        try {
            log.debug("Looking for pool of players with pool id " + playerPoolId);
            return contactRepository.getPlayerPoolById(playerPoolId);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to find pool for id " + playerPoolId, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rrabarg.teamcaptain.service.PlayerPoolService#clearPlayers(java.lang.String,
     * org.rrabarg.teamcaptain.domain.PlayerPool)
     */
    @Override
    public void clearPlayers(String name, PlayerPool playerPool) {
        try {
            contactRepository.clearPlayersFromPool(playerPool.getId());
            contactRepository.deletePlayerPoolsByName(name);
        } catch (IOException | ServiceException e) {
            throw new RuntimeException("Failed to clear players from pool " + playerPool);
        }
    }
}
