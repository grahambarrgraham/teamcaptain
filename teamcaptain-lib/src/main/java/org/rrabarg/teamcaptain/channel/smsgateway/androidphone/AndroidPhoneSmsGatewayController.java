package org.rrabarg.teamcaptain.channel.smsgateway.androidphone;

import java.time.Clock;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.channel.SmsMessage;
import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.Reactor;
import reactor.event.Event;

@Controller
public class AndroidPhoneSmsGatewayController {

    @Autowired
    Reactor reactor;

    @Autowired
    Provider<Clock> clock;

    @RequestMapping(value = "/androidsmshandler", method = RequestMethod.GET)
    public void receiveSms(@RequestParam String phone, @RequestParam String smscenter, @RequestParam String text) {

        reactor.notify(ReactorMessageKind.InboundChannelMessage,
                new Event<>(new SmsMessage(phone, text, clock.get().instant())));
    }
}
