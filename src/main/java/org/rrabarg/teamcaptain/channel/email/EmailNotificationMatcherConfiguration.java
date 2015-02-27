package org.rrabarg.teamcaptain.channel.email;

import org.rrabarg.teamcaptain.channel.NotificationMatcherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailNotificationMatcherConfiguration {

    @Bean
    NotificationMatcherService emailNotificationMatcherService() {
        return new NotificationMatcherService(player -> player.getEmailAddress());
    }

}
