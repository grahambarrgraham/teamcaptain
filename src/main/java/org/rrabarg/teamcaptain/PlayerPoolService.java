package org.rrabarg.teamcaptain;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.PoolOfPlayers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gdata.util.ServiceException;

@Component
public class PlayerPoolService {

    @Autowired
    PoolOfPlayersRepository contactRepository;

    public String savePlayerPool(String name, final PoolOfPlayers playerPool) {

        try {
            String poolId = playerPool.getId();

            if (poolId == null) {
                poolId = getOrCreatePlayerPoolId(name);
                playerPool.setId(poolId);
            }

            contactRepository.addPlayersToPool(poolId, playerPool.getPlayers());
            return poolId;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to save player pool " + playerPool + " for competition " + name, e);
        }
    }

    private String getOrCreatePlayerPoolId(String competitionName) throws IOException, InterruptedException {

        try {
            String poolId = contactRepository.findPlayerPoolIdByName(competitionName);
            if (poolId == null) {
                poolId = contactRepository.createPlayerPoolWithName(competitionName);
            }

            return poolId;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to get or create player pool id for competition " + competitionName, e);
        }
    }

    public PoolOfPlayers findById(String playerPoolId) {
        try {
            return contactRepository.getPlayerPoolById(playerPoolId);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to find pool for id " + playerPoolId);
        }
    }

    public void clearPlayers(PoolOfPlayers playerPool) {
        try {
            contactRepository.clearPlayersFromPool(playerPool.getId());
        } catch (IOException | ServiceException e) {
            throw new RuntimeException("Failed to clear players from pool " + playerPool);
        }
    }

}
