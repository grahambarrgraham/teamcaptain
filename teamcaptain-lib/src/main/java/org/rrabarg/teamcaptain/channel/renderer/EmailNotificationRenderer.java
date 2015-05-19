package org.rrabarg.teamcaptain.channel.renderer;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.channel.Email;
import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.channel.NotificationRenderer;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationRenderer implements NotificationRenderer {

    @Autowired
    Provider<Clock> clock;

    @Override
    public Message render(Notification notification) {
        return renderer(notification).build();
    }

    private EmailNotificationBuilder renderer(Notification notification) {
        return new EmailNotificationBuilder(notification);
    }

    class EmailNotificationBuilder {

        private static final String NEW_LINE = "\n";
        private final Notification notification;

        EmailNotificationBuilder(Notification notification) {
            this.notification = notification;
        }

        Email build() {
            final String toAddress = notification.getTargetContactDetail().getEmailAddress();

            ContentBuilder contentBuilder = null;
            ContentBuilder subjectBuilder = null;

            switch (notification.getKind()) {
            case CanYouPlay:
                subjectBuilder = subject().matchTitle();
                contentBuilder = content().canYouPlayContent();
                break;
            case ConfirmationOfAcceptance:
                subjectBuilder = subject().matchTitle();
                contentBuilder = content().confirmAcceptance();
                break;
            case ConfirmationOfStandby:
                subjectBuilder = subject().matchTitle();
                contentBuilder = content().confirmStandby();
                break;
            case ConfirmationOfDecline:
                subjectBuilder = subject().matchTitle();
                contentBuilder = content().confirmDecline();
                break;
            case Reminder:
                subjectBuilder = subject().add("REMINDER: ").matchTitle();
                contentBuilder = content().reminder();
                break;
            case MatchConfirmation:
                subjectBuilder = subject().add("Match Confirmed: ").matchTitle();
                contentBuilder = content().matchConfirmation();
            case MatchStatus:
                subjectBuilder = subject().add("Match Status Update : ").matchTitle();
                contentBuilder = content().matchStatus();
                break;
            case StandBy:
                subjectBuilder = subject().matchTitle();
                contentBuilder = content().canYouStandby();
                break;
            case StandDown:
                break;
            case InsufficientPlayers:
            case OutOfBandMessage:
            case StandbyPlayersNotified:
                subjectBuilder = subject().add("Match Alert : ").matchTitle();
                contentBuilder = content().alert(notification.getKind());
            default:
                break;
            }

            return new Email(
                    subjectBuilder.build(),
                    toAddress,
                    notification.getTeamCaptain().getEmailAddress(),
                    contentBuilder.build(),
                    now());
        }

        private Instant now() {
            return clock.get().instant();
        }

        private ContentBuilder subject() {
            return new ContentBuilder();
        }

        private ContentBuilder content() {
            return new ContentBuilder();
        }

        class ContentBuilder {
            StringBuilder builder = new StringBuilder();

            public ContentBuilder add(String s) {
                builder.append(s);
                return this;
            }

            public ContentBuilder matchStatus() {
                this
                        .hello()
                        .append("A full team has not yet been selected for ")
                        .matchTitle()
                        .append(" on ")
                        .matchDate()
                        .append(".")
                        .newline()
                        .playerStatus()
                        .newline()
                        .signoff();
                return this;
            }

            private ContentBuilder playerStatus() {
                final List<Player> acceptedPlayers = getMatch().getAcceptedPlayers(notification.getPlayerPool());
                final List<Player> declinedPlayers = getMatch().getDeclinedPlayers(notification.getPlayerPool());
                final List<Player> notifiedPlayers = getMatch().getNotifiedPlayers(notification.getPlayerPool());
                final List<Player> onStandbyPlayers = getMatch().getAcceptedOnStandbyPlayers(
                        notification.getPlayerPool());

                return playerStatusDetail(acceptedPlayers, "accepted")
                        .playerStatusDetail(declinedPlayers, "declined")
                        .playerStatusDetail(notifiedPlayers, "yet to respond")
                        .playerStatusDetail(onStandbyPlayers, "accepted a standby request");
            }

            private ContentBuilder playerStatusDetail(List<Player> player, String description) {
                if (player.isEmpty()) {
                    this.append("No players have ").append(description).append(".");
                } else {
                    this.append("The following players have ").append(description).append(" : ");
                    this.append(player.toString());
                }
                this.append(".").newline();
                return this;
            }

            public ContentBuilder matchConfirmation() {
                this
                        .hello()
                        .append("Please find the confirmed team selection below for ")
                        .matchTitle()
                        .append(".")
                        .newline()
                        .matchDetails()
                        .newline()
                        .travelDetails()
                        .newline()
                        .signoff();
                return this;
            }

            private ContentBuilder travelDetails() {
                return this.append(getMatch().getTravelDetails());
            }

            private ContentBuilder matchDetails() {
                this.append("The match is at ")
                        .append(getMatch().getLocation().toString())
                        .append(" and starts at ")
                        .matchStartTime()
                        .append(" on ")
                        .matchDate()
                        .append(".")
                        .newline()
                        .teamForMatch();
                return this;
            }

            private ContentBuilder teamForMatch() {
                this.append("The team for this match will be : ");
                this.append(getMatch().getAcceptedPlayers(notification.getPlayerPool()).toString());
                return this;
            }

            private ContentBuilder matchStartTime() {
                return this.append(getMatch().getStartDateTime()
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            }

            public ContentBuilder canYouStandby() {
                this
                        .hello()
                        .append("Can you standby for the match on ")
                        .matchDate()
                        .append(".")
                        .newline()
                        .answerYesOrNo()
                        .add(". If yes, I'll be in touch shortly to confirm once I've contacted all the players.")
                        .signoff();
                return this;
            }

            public ContentBuilder confirmStandby() {
                this
                        .hello()
                        .append("Brilliant, thanks, I'll be in touch shortly to confirm.")
                        .append(NEW_LINE)
                        .signoff();
                return this;
            }

            public ContentBuilder append(String s) {
                if (s != null) {
                    builder.append(s);
                }
                return this;
            }

            public String build() {
                return builder.toString();
            }

            public ContentBuilder matchTitle() {
                builder.append(getMatch().getTitle());
                return this;
            }

            private Match getMatch() {
                return notification.getMatch();
            }

            public ContentBuilder reminder() {
                this
                        .hello()
                        .append("Sorry to bother you again, but its getting close to the match date and we really need to know whether you can play. The match is on : ")
                        .matchDate()
                        .append(".")
                        .newline()
                        .answerYesOrNo()
                        .signoff();
                return this;
            }

            public ContentBuilder canYouPlayContent() {
                this
                        .hello()
                        .append("Can you play in this match on ")
                        .matchDate()
                        .append(".")
                        .newline()
                        .answerYesOrNo()
                        .signoff();
                return this;
            }

            public ContentBuilder confirmAcceptance() {
                this
                        .hello()
                        .append("Brilliant, your in. I'll be in touch shortly with all the match details once I've got a full team.")
                        .append(NEW_LINE)
                        .signoff();
                return this;
            }

            public ContentBuilder confirmDecline() {
                this.hello()
                        .append("Sorry you couldn't play, hope too see you in future matches.")
                        .append(NEW_LINE)
                        .signoff();
                return this;
            }

            private ContentBuilder newline() {
                this.append(NEW_LINE);
                return this;
            }

            private ContentBuilder matchDate() {
                this.append(getMatch().getStartDateTime()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                return this;
            }

            public ContentBuilder signoff() {
                this
                        .append("Thanks")
                        .append(NEW_LINE)
                        .append(notification.getTeamCaptain().getFirstname());
                return this;
            }

            public ContentBuilder answerYesOrNo() {
                this
                        .append("Please reply with the text YES or NO. ")
                        .append("If you're not sure please reply with any details. Once I've got a full team, I'll send out all of the match details.")
                        .append(NEW_LINE);
                return this;
            }

            public ContentBuilder hello() {
                this
                        .append("Hi ")
                        .append(notification.getTargetContactDetail().getFirstname())
                        .append(NEW_LINE)
                        .append(NEW_LINE);
                return this;
            }

            public ContentBuilder alert(NotificationKind kind) {
                this
                        .hello()
                        .append("Alert : " + kind)
                        .matchTitle()
                        .append(" on ")
                        .matchDate()
                        .append(".")
                        .playerStatus()
                        .signoff();
                return this;
            }

        }
    }

}
