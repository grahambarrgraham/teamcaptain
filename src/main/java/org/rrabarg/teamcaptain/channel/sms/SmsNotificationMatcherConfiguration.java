package org.rrabarg.teamcaptain.channel.sms;

import org.rrabarg.teamcaptain.channel.NotificationMatcherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsNotificationMatcherConfiguration {

    @Bean
    NotificationMatcherService smsNotificationMatcherService() {
        return new NotificationMatcherService(player -> player.getMobileNumber());
    }

}
