package org.rrabarg.teamcaptain.service;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerNotificationRenderer {

    @Autowired
    Provider<Clock> clock;

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

            SubjectBuilder contentBuilder = null;
            SubjectBuilder subjectBuilder = null;

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
                subjectBuilder = subject().add("Match confirmed: ").matchTitle();
                contentBuilder = content().matchConfirmation();
                break;
            case StandBy:
                subjectBuilder = subject().matchTitle();
                contentBuilder = content().canYouStandby();
                break;
            case StandDown:
                break;
            default:
                break;
            }

            return new Email(
                    subjectBuilder.build(),
                    toAddress,
                    getOutboundEmailAddress(),
                    contentBuilder.build(),
                    now());
        }

        private Instant now() {
            return clock.get().instant();
        }

        private SubjectBuilder subject() {
            return new SubjectBuilder();
        }

        private SubjectBuilder content() {
            return new SubjectBuilder();
        }

        class SubjectBuilder {
            StringBuilder builder = new StringBuilder();

            public SubjectBuilder add(String s) {
                builder.append(s);
                return this;
            }

            public SubjectBuilder matchConfirmation() {
                this
                        .hello()
                        .append("Here's the details for ")
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

            private SubjectBuilder travelDetails() {
                return this.append(getMatch().getTravelDetails());
            }

            private SubjectBuilder matchDetails() {
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

            private SubjectBuilder teamForMatch() {
                this.append("The team for this match will be : ");
                this.append(getMatch().getAcceptedPlayers(notification.getPoolOfPlayers()).toString());
                return this;
            }

            private SubjectBuilder matchStartTime() {
                return this.append(getMatch().getStartDateTime()
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            }

            public SubjectBuilder canYouStandby() {
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

            public SubjectBuilder confirmStandby() {
                this
                        .hello()
                        .append("Brilliant, thanks, I'll be in touch shortly to confirm.")
                        .append(NEW_LINE)
                        .signoff();
                return this;
            }

            public SubjectBuilder append(String s) {
                if (s != null) {
                    builder.append(s);
                }
                return this;
            }

            public String build() {
                return builder.toString();
            }

            public SubjectBuilder matchTitle() {
                builder.append(getMatch().getTitle());
                return this;
            }

            private Match getMatch() {
                return notification.getMatch();
            }

            public SubjectBuilder reminder() {
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

            public SubjectBuilder canYouPlayContent() {
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

            public SubjectBuilder confirmAcceptance() {
                this
                        .hello()
                        .append("Brilliant, your in. I'll be in touch shortly with all the match details once I've got a full team.")
                        .append(NEW_LINE)
                        .signoff();
                return this;
            }

            public SubjectBuilder confirmDecline() {
                this.hello()
                        .append("Sorry you couldn't play, hope too see you in future matches.")
                        .append(NEW_LINE)
                        .signoff();
                return this;
            }

            private SubjectBuilder newline() {
                this.append(NEW_LINE);
                return this;
            }

            private SubjectBuilder matchDate() {
                this.append(getMatch().getStartDateTime()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                return this;
            }

            public SubjectBuilder signoff() {
                this
                        .append("Thanks")
                        .append(NEW_LINE)
                        .append(notification.getOrganiserFirstName());
                return this;
            }

            public SubjectBuilder answerYesOrNo() {
                this
                        .append("Please reply with the text YES or NO. ")
                        .append("If you're not sure please reply with any details. Once I've got a full team, I'll send out all of the match details.")
                        .append(NEW_LINE);
                return this;
            }

            public SubjectBuilder hello() {
                this
                        .append("Hi ")
                        .append(notification.getPlayer().getFirstname())
                        .append(NEW_LINE)
                        .append(NEW_LINE);
                return this;
            }

        }

        private String getOutboundEmailAddress() {
            return "grahambarrgraham@gmail.com";
        }
    }

}
