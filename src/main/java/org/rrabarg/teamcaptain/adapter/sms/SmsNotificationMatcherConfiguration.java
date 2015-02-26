package org.rrabarg.teamcaptain.adapter.sms;

import org.rrabarg.teamcaptain.adapter.NotificationMatcherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsNotificationMatcherConfiguration {

    @Bean
    NotificationMatcherService smsNotificationMatcherService() {
        return new NotificationMatcherService(player -> player.getMobileNumber());
    }

}
