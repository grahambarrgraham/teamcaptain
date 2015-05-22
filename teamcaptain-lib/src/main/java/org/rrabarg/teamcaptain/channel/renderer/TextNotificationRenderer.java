package org.rrabarg.teamcaptain.channel.renderer;

import java.time.Clock;
import java.time.Instant;

import javax.inject.Provider;

import org.mortbay.log.Log;
import org.rrabarg.teamcaptain.channel.Email;
import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.channel.NotificationRenderer;
import org.rrabarg.teamcaptain.channel.SmsMessage;
import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;

public class TextNotificationRenderer implements NotificationRenderer {

    @Autowired
    Provider<Clock> clock;

    Channel channel;

    public TextNotificationRenderer(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Message render(Notification notification) {
        switch (channel) {
        case Email:
            return renderer(notification).buildSimpleTextEmail();
        default:
            return renderer(notification).buildSms();
        }
    }

    private SmsNotificationBuilder renderer(Notification notification) {
        return new SmsNotificationBuilder(notification);
    }

    class SmsNotificationBuilder {

        private final Notification notification;
        private final TextContentBuilder contentBuilder;
        private final TextContentBuilder subjectBuilder;

        SmsNotificationBuilder(Notification notification) {
            this.notification = notification;
            contentBuilder = new TextContentBuilder(notification);
            subjectBuilder = new TextContentBuilder(notification);

            switch (notification.getKind()) {
            case CanYouPlay:
                subject().matchTitleAndDate();
                content().canYouPlayContent();
                break;
            case ConfirmationOfAcceptance:
                subject().matchTitleAndDate();
                content().confirmAcceptance();
                break;
            case ConfirmationOfStandby:
                subject().matchTitleAndDate();
                content().confirmStandby();
                break;
            case ConfirmationOfDecline:
                subject().matchTitleAndDate();
                content().confirmDecline();
                break;
            case Reminder:
                subject().add("REMINDER: ").matchTitleAndDate();
                content().reminder();
                break;
            case MatchFulfilled:
            case MatchConfirmation:
                subject().add("Match Confirmed: ").matchTitleAndDate();
                content().matchConfirmation();
                break;
            case MatchStatus:
                subject().add("Match Status Update : ").matchTitleAndDate();
                content().matchStatus();
                break;
            case StandBy:
                subject().matchTitleAndDate();
                content().canYouStandby();
                break;
            case StandDown:
                subject().matchTitleAndDate();
                content().standdown();
                break;
            case InsufficientPlayers:
            case OutOfBandMessage:
            case StandbyPlayersNotified:
                subject().add("Match Alert : ").matchTitle();
                content().alert(notification.getKind());
            default:
                Log.warn("Email rendering did not know how to render %s for %s", notification.getKind(),
                        notification.getTarget());
                break;
            }
        }

        private TextContentBuilder subject() {
            return subjectBuilder;
        }

        private TextContentBuilder content() {
            return contentBuilder;
        }

        public Message buildSms() {
            return new SmsMessage(
                    notification.getTargetContactDetail().getMobileNumber(),
                    subjectBuilder.space().append(contentBuilder.build()).build(),
                    now());
        }

        public Message buildSimpleTextEmail() {
            return new Email(
                    subjectBuilder.build(),
                    notification.getTargetContactDetail().getEmailAddress(),
                    notification.getTeamCaptain().getEmailAddress(), // question this, emails will go back to team
                                                                     // captain, not system!
                    contentBuilder.build(),
                    now());
        }

        private Instant now() {
            return clock.get().instant();
        }

    }

}
