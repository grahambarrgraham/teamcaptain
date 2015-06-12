package org.rrabarg.teamcaptain.channel.renderer;

import org.rrabarg.teamcaptain.channel.NotificationRenderer;
import org.rrabarg.teamcaptain.domain.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RendererConfiguration {

    @Bean
    public NotificationRenderer emailNotificationRenderer() {
        return new TextNotificationRenderer(Channel.Email);
    }

    @Bean
    NotificationRenderer smsNotificationRenderer() {
        return new TextNotificationRenderer(Channel.Sms);
    }

}
