package org.rrabarg.teamcaptain.channel.sms;

import java.time.Clock;
import java.time.Instant;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsPlayerNotificationRenderer {

    @Autowired
    Provider<Clock> clock;

    public SmsMessage render(PlayerNotification notification) {
        return renderer(notification).build();
    }

    private SmsNotificationBuilder renderer(PlayerNotification notification) {
        return new SmsNotificationBuilder(notification);
    }

    class SmsNotificationBuilder {

        final PlayerNotification notification;

        SmsNotificationBuilder(PlayerNotification notification) {
            this.notification = notification;
        }

        SmsMessage build() {

            final SmsContentBuilder builder = new SmsContentBuilder(notification);

            switch (notification.getKind()) {
            case CanYouPlay:
                builder.canYouPlayContent();
                break;
            case ConfirmationOfAcceptance:
                builder.confirmAcceptance();
                break;
            case ConfirmationOfStandby:
                builder.confirmStandby();
                break;
            case ConfirmationOfDecline:
                builder.confirmDecline();
                break;
            case Reminder:
                builder.reminder();
                break;
            case MatchConfirmation:
                builder.matchConfirmation();
                break;
            case StandBy:
                builder.canYouStandby();
                break;
            case StandDown:
                break;
            default:
                return null;
            }

            return new SmsMessage(
                    notification.getPlayer().getMobileNumber(),
                    builder.build(),
                    now());
        }

        private Instant now() {
            return clock.get().instant();
        }

    }

}
