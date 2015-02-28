package org.rrabarg.teamcaptain.channel.renderer;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.channel.Email;
import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.channel.NotificationRenderer;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailAdminAlertRenderer implements NotificationRenderer {

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

            SubjectBuilder contentBuilder = null;
            SubjectBuilder subjectBuilder = null;

            switch (notification.getKind()) {
            case MatchFulfilled:
                subjectBuilder = subject().add("Match confirmation: ").matchTitle();
                contentBuilder = content().matchConfirmation();
                break;
            default:
                subjectBuilder = subject().add("Alert ").kind().add(" : ").matchTitle();
                contentBuilder = content().matchDetail();
                break;
            }

            return new Email(
                    subjectBuilder.build(),
                    toAddress,
                    toAddress,
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

            public SubjectBuilder matchDetail() {
                builder.append(notification.getMatch().toString());
                return this;
            }

            public SubjectBuilder kind() {
                builder.append(notification.getKind().toString());
                return this;
            }

            public SubjectBuilder append(String s) {
                if (s != null) {
                    builder.append(s);
                }
                return this;
            }

            private SubjectBuilder matchDate() {
                this.append(getMatch().getStartDateTime()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                return this;
            }

            public String build() {
                return builder.toString();
            }

            public SubjectBuilder matchConfirmation() {
                this
                        .append("Here's the details for ")
                        .matchTitle()
                        .append(".")
                        .newline()
                        .matchDetails()
                        .newline()
                        .travelDetails()
                        .newline();
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
                this.append(getMatch().getAcceptedPlayers(notification.getPlayerPool()).toString());
                return this;
            }

            private SubjectBuilder matchStartTime() {
                return this.append(getMatch().getStartDateTime()
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            }

            public SubjectBuilder matchTitle() {
                builder.append(notification.getMatch().getTitle());
                return this;
            }

            private Match getMatch() {
                return notification.getMatch();
            }

            private SubjectBuilder newline() {
                this.append(NEW_LINE);
                return this;
            }

        }
    }

}
