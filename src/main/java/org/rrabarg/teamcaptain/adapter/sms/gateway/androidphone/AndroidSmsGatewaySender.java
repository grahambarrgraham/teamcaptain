package org.rrabarg.teamcaptain.adapter.sms.gateway.androidphone;

import static reactor.event.selector.Selectors.$;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.rrabarg.teamcaptain.adapter.sms.SmsMessage;
import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Component
public class AndroidSmsGatewaySender implements Consumer<Event<SmsMessage>> {

    private static final String PASSWORD = "";
    private static final String PHONE_URL = "192.168.0.6:9090/sendsms";

    @Inject
    RestTemplate restTemplate;

    @Inject
    Reactor reactor;

    void sendMessage(SmsMessage message) {
        restTemplate.getForObject(getUrl(), Void.class, mapFor(message));
    }

    Map<String, Object> mapFor(SmsMessage message) {
        final Map<String, Object> map = new HashMap<>();
        map.put("phone", message.getPhone());
        map.put("text", message.getText());
        map.put("password", getPassword());
        return map;
    }

    private String getUrl() {
        return PHONE_URL;
    }

    private String getPassword() {
        return PASSWORD;
    }

    @Override
    public void accept(Event<SmsMessage> t) {
        reactor.on($(ReactorMessageKind.OutboundSms), this);
    }
}
