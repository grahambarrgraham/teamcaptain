package org.rrabarg.teamcaptain.adapter.email;

import org.rrabarg.teamcaptain.adapter.NotificationMatcherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailNotificationMatcherConfiguration {

    @Bean
    NotificationMatcherService emailNotificationMatcherService() {
        return new NotificationMatcherService(player -> player.getEmailAddress());
    }

}
