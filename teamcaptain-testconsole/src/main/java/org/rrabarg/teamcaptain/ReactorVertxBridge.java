package org.rrabarg.teamcaptain;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.springframework.stereotype.Component;
import org.vertx.java.core.eventbus.EventBus;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Component
public class ReactorVertxBridge implements Consumer<Event<Message>> {

    private static final String VERTX_ADDRESS = "reactorvertxbridge";

    @Inject
    Reactor reactor;

    @Inject
    Provider<Clock> clock;

    @Inject
    EventBus eventBus;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.OutboundEmail), this);
        reactor.on($(ReactorMessageKind.OutboundSms), this);

        eventBus.registerHandler(VERTX_ADDRESS, event -> {

            final Message message = ((Message) event.body()).withTimestamp(clock.get().instant());

            reactor.notify(ReactorMessageKind.InboundChannelMessage,
                    new Event<>(message));
        });

    }

    @Override
    public void accept(Event<Message> message) {
        eventBus.publish(VERTX_ADDRESS, message);
    }
}
