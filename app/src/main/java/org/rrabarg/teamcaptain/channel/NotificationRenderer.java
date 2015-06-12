package org.rrabarg.teamcaptain.channel;

import org.rrabarg.teamcaptain.domain.Notification;

public interface NotificationRenderer {

    Message render(Notification notification);

}
