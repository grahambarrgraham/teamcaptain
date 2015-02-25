package org.rrabarg.teamcaptain.service;

import org.rrabarg.teamcaptain.domain.PlayerPool;

public interface PlayerPoolService {

    String savePlayerPool(String name, PlayerPool playerPool);

    PlayerPool findById(String playerPoolId);

    void clearPlayers(String name, PlayerPool playerPool);

}