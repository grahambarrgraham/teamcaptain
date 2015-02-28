package org.rrabarg.teamcaptain.channel;

import static org.rrabarg.teamcaptain.domain.Channel.Email;
import static org.rrabarg.teamcaptain.domain.Channel.Sms;
import static org.rrabarg.teamcaptain.domain.User.UserRole.Player;
import static org.rrabarg.teamcaptain.domain.User.UserRole.TeamCaptain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.User.UserRole;
import org.springframework.stereotype.Component;

@Component
public class NotificationRendererFactory {

    Map<Key, NotificationRenderer> map = new HashMap<>();

    @Inject
    NotificationRenderer emailAdminAlertRenderer;

    @Inject
    NotificationRenderer smsAdminAlertRenderer;

    @Inject
    NotificationRenderer emailPlayerNotificationRenderer;

    @Inject
    NotificationRenderer smsPlayerNotificationRenderer;

    @PostConstruct
    void initMap() {
        map.put($(TeamCaptain, Email), emailAdminAlertRenderer);
        map.put($(TeamCaptain, Sms), smsAdminAlertRenderer);
        map.put($(Player, Email), emailPlayerNotificationRenderer);
        map.put($(Player, Sms), smsPlayerNotificationRenderer);
    }

    public Key $(UserRole target, Channel email) {
        return new Key(target, email);
    }

    public NotificationRenderer getRenderer(Notification notification, Channel channel) {
        return getRendererFor(notification.getTarget().getRole(), channel).get();
    }

    private Optional<NotificationRenderer> getRendererFor(UserRole role, Channel channel) {
        return Optional.of(map.get($(role, channel)));
    }

    class Key {
        private final UserRole target;
        private final Channel channel;

        public Key(UserRole target, Channel channel) {
            this.target = target;
            this.channel = channel;
        }

        @Override
        public int hashCode() {
            return Objects.hash(target, channel);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }
    }

}
