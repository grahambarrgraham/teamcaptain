package org.rrabarg.teamcaptain.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
class State implements Consumer<Event<Integer>> {

    @Autowired
    Reactor reactor;

    @Override
    public void accept(Event<Integer> ev) {
        //
    }

    public void publishEvent(int numberOfQuotes) {
        reactor.notify("quotes", Event.wrap(1));
    }

}
