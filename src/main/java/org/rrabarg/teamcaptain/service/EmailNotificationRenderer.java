package org.rrabarg.teamcaptain.service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationRenderer {

    public Email render(PlayerNotification notification) {
        return renderer(notification).build();
    }

    private EmailNotificationBuilder renderer(PlayerNotification notification) {
        return new EmailNotificationBuilder(notification);
    }

    class EmailNotificationBuilder {

        private static final String NEW_LINE = "\n";
        private final PlayerNotification notification;

        EmailNotificationBuilder(PlayerNotification notification) {
            this.notification = notification;
        }

        Email build() {
            final String toAddress = notification.getPlayer().getEmailAddress();
            final String title = notification.getMatch().getTitle();

            StringBuilder bob = null;

            switch (notification.getKind()) {
            case CanYouPlay:
                bob = canYouPlay();
                break;
            case ConfirmationOfAcceptance:
                bob = confirmAcceptance();
                break;
            case ConfirmationOfDecline:
                bob = confirmDecline();
                break;
            case Reminder:
                break;
            case StandBy:
                break;
            case StandDown:
                break;
            default:
                break;
            }

            return new Email(title, toAddress, getOutboundEmailAddress(), bob.toString());
        }

        private StringBuilder canYouPlay() {
            final StringBuilder bob = new StringBuilder();
            bob
                    .append("Hi ")
                    .append(notification.getPlayer().getFirstname())
                    .append(NEW_LINE)
                    .append(NEW_LINE)
                    .append("Can you play in this match on ")
                    .append(notification.getMatch().getStartDateTime()
                            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)))
                    .append(".")
                    .append(NEW_LINE)
                    .append("Please reply with the text YES or NO. ")
                    .append("If you're not sure please reply with any details. Once I've got a full team, I'll send out all of the match details.")
                    .append(NEW_LINE)
                    .append("Thanks")
                    .append(NEW_LINE)
                    .append(notification.getOrganiserFirstName());
            return bob;
        }

        private StringBuilder confirmAcceptance() {
            final StringBuilder bob = new StringBuilder();
            bob
                    .append("Hi ")
                    .append(notification.getPlayer().getFirstname())
                    .append(NEW_LINE)
                    .append(NEW_LINE)
                    .append("Brilliant, your in. I'll be in touch shortly with all the match details once I've got a full team.")
                    .append(NEW_LINE)
                    .append("Thanks")
                    .append(NEW_LINE)
                    .append(notification.getOrganiserFirstName());
            return bob;
        }

        private StringBuilder confirmDecline() {
            final StringBuilder bob = new StringBuilder();
            bob
                    .append("Hi ")
                    .append(notification.getPlayer().getFirstname())
                    .append(NEW_LINE)
                    .append(NEW_LINE)
                    .append("Sorry you couldn't play, hope too see you in future matches.")
                    .append(NEW_LINE)
                    .append("Thanks")
                    .append(NEW_LINE)
                    .append(notification.getOrganiserFirstName());
            return bob;
        }

        private String getOutboundEmailAddress() {
            return "grahambarrgraham@gmail.com";
        }
    }

}
