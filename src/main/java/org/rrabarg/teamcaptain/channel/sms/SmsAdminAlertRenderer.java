package org.rrabarg.teamcaptain.channel.sms;

import java.time.Clock;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.TeamCaptainNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsAdminAlertRenderer {

    @Autowired
    Provider<Clock> clock;

    public SmsMessage render(TeamCaptainNotification notification) {

        final SmsContentBuilder builder = new SmsContentBuilder(notification);

        switch (notification.getKind()) {
        case MatchFulfilled:
            builder.matchConfirmation();
            break;
        default:
            builder.matchDescription();
            break;
        }

        return new SmsMessage(notification.getTargetContact().getMobileNumber(), builder.build(), clock.get().instant());
    }

}
