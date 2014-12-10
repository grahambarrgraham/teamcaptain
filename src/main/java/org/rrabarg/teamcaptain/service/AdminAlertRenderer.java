package org.rrabarg.teamcaptain.service;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.AdminAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminAlertRenderer {

    @Autowired
    Provider<Clock> clock;

    public Email render(AdminAlert notification) {
        return renderer(notification).build();
    }

    private EmailNotificationBuilder renderer(AdminAlert notification) {
        return new EmailNotificationBuilder(notification);
    }

    class EmailNotificationBuilder {

        private static final String NEW_LINE = "\n";
        private final AdminAlert notification;

        EmailNotificationBuilder(AdminAlert notification) {
            this.notification = notification;
        }

        Email build() {
            final String toAddress = getOutboundEmailAddress();

            SubjectBuilder contentBuilder = null;
            SubjectBuilder subjectBuilder = null;

            switch (notification.getKind()) {
            default:
                subjectBuilder = subject().add("Alert ").kind().add(" : ").matchTitle();
                contentBuilder = content().matchDetail();
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

            public SubjectBuilder matchDetail() {
                builder.append(notification.getMatch().toString());
                return this;
            }

            public SubjectBuilder kind() {
                builder.append(notification.getKind().toString());
                return this;
            }

            public SubjectBuilder append(String s) {
                builder.append(s);
                return this;
            }

            public String build() {
                return builder.toString();
            }

            public SubjectBuilder matchTitle() {
                builder.append(notification.getMatch().getTitle());
                return this;
            }

            private SubjectBuilder newline() {
                this.append(NEW_LINE);
                return this;
            }

            private SubjectBuilder theMatch() {
                this.append(notification.getMatch().getStartDateTime()
                        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                return this;
            }

        }

        private String getOutboundEmailAddress() {
            return "grahambarrgraham@gmail.com";
        }
    }

}
