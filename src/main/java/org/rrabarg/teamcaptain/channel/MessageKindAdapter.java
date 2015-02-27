package org.rrabarg.teamcaptain.channel;

import org.rrabarg.teamcaptain.domain.Player;

public interface MessageKindAdapter {

    String getPlayerAddress(Player player);

}
