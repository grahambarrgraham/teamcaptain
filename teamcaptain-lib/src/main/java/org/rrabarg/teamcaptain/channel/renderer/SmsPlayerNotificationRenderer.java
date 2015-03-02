package org.rrabarg.teamcaptain.channel.renderer;

import java.time.Clock;
import java.time.Instant;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.channel.NotificationRenderer;
import org.rrabarg.teamcaptain.channel.SmsMessage;
import org.rrabarg.teamcaptain.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsPlayerNotificationRenderer implements NotificationRenderer {

    @Autowired
    Provider<Clock> clock;

    @Override
    public Message render(Notification notification) {
        return renderer(notification).build();
    }

    private SmsNotificationBuilder renderer(Notification notification) {
        return new SmsNotificationBuilder(notification);
    }

    class SmsNotificationBuilder {

        final Notification notification;

        SmsNotificationBuilder(Notification notification) {
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
                    notification.getTargetContactDetail().getMobileNumber(),
                    builder.build(),
                    now());
        }

        private Instant now() {
            return clock.get().instant();
        }

    }

}
