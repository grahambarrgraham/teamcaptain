package org.rrabarg.teamcaptain.channel.renderer;

import java.time.Clock;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.channel.NotificationRenderer;
import org.rrabarg.teamcaptain.channel.SmsMessage;
import org.rrabarg.teamcaptain.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsAdminAlertRenderer implements NotificationRenderer {

    @Autowired
    Provider<Clock> clock;

    @Override
    public Message render(Notification notification) {

        final SmsContentBuilder builder = new SmsContentBuilder(notification);

        switch (notification.getKind()) {
        case MatchFulfilled:
            builder.matchConfirmation();
            break;
        default:
            builder.matchDescription();
            break;
        }

        return new SmsMessage(notification.getTargetContactDetail().getMobileNumber(), builder.build(), clock.get()
                .instant());
    }

}
