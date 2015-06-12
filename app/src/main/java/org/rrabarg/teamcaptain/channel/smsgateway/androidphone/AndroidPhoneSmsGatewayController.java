package org.rrabarg.teamcaptain.channel.smsgateway.androidphone;

import org.rrabarg.teamcaptain.channel.SmsMessage;
import org.rrabarg.teamcaptain.domain.ReactorMessageKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import java.time.Clock;
import java.util.concurrent.ConcurrentLinkedDeque;

import static reactor.event.selector.Selectors.$;

@RestController
@Profile("androidsms")
public class AndroidPhoneSmsGatewayController implements Consumer<Event<SmsMessage>> {

    @Autowired
    Reactor reactor;

    @Autowired
    Provider<Clock> clock;

    ConcurrentLinkedDeque deque = new ConcurrentLinkedDeque();

    private String secretKey;

    @PostConstruct
    public void setup() {
        secretKey = "testkey";
        reactor.on($(ReactorMessageKind.OutboundSms), this);
    }

    @RequestMapping(value = "/androidsms", method = RequestMethod.POST)
    public @ResponseBody SmsSyncResponse receiveMessage(@ModelAttribute("customer") SyncMessage syncMessage, BindingResult result) {

        if (result.hasErrors()) {
            return new SmsSyncResponse(true, result.getAllErrors().toString());
        }

        reactor.notify(ReactorMessageKind.InboundChannelMessage,
                new Event<>(new SmsMessage(syncMessage.getFrom(), syncMessage.getMessage(), clock.get().instant())));

        return new SmsSyncResponse(true, null);
    }

    @RequestMapping(value = "/androidsms", method = RequestMethod.GET)
    public @ResponseBody SmsSyncResponse getQueuedMessages(@RequestParam String task) {
        return new SmsSyncResponse(secretKey, deque.stream());
    }

    @Override
    public void accept(Event<SmsMessage> smsMessageEvent) {
        deque.addLast(smsMessageEvent);
    }


}
