package org.rrabarg.teamcaptain.channel;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.rrabarg.teamcaptain.channel.renderer.SmsAdminAlertRenderer;
import org.rrabarg.teamcaptain.channel.renderer.SmsPlayerNotificationRenderer;
import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class OutboundChannelService implements Consumer<Event<Notification>> {

    static Logger log = LoggerFactory.getLogger(OutboundChannelService.class);

    @Autowired
    Reactor reactor;

    @Autowired
    SmsPlayerNotificationRenderer playerNotificationRenderer;

    @Autowired
    SmsAdminAlertRenderer adminAlertRenderer;

    @Autowired
    ChannelResolverService channelResolverService;

    @Autowired
    NotificationRendererFactory notificationRendererFactory;

    @Autowired
    Provider<Clock> clock;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundNotification), this);
    }

    @Override
    public void accept(Event<Notification> event) {

        final Notification notification = event.getData();
        final Set<Channel> channels = channelResolverService.getChannels(notification);
        for (final Channel channel : channels) {
            final NotificationRenderer renderer = notificationRendererFactory.getRenderer(notification, channel);
            final Message renderedMessage = renderer.render(notification);
            log.debug("Sending outbound message : " + renderedMessage + " on channel " + channel);
            reactor.notify(channel.getOutgoingMessageKind(), new Event<>(renderedMessage));
        }
    }
}
