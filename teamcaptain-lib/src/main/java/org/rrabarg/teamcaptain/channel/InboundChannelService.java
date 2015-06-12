package org.rrabarg.teamcaptain.channel;

import static reactor.event.selector.Selectors.$;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.rrabarg.teamcaptain.channel.NotificationMatcherService.MatcherResult;
import org.rrabarg.teamcaptain.channel.NotificationMatcherService.MatcherResultItem;
import org.rrabarg.teamcaptain.domain.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.MatchWorkflow;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
import org.rrabarg.teamcaptain.domain.PlayerResponse.Kind;
import org.rrabarg.teamcaptain.domain.User.UserRole;
import org.rrabarg.teamcaptain.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class InboundChannelService implements Consumer<Event<Message>> {

    static Logger log = LoggerFactory.getLogger(InboundChannelService.class);

    @Inject
    private Reactor reactor;

    @Inject
    private NotificationMatcherService notificationMatcherService;

    @Inject
    private WorkflowService workflowService;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.InboundChannelMessage), this);
    }

    @Override
    public void accept(Event<Message> event) {

        final Message message = event.getData();

        try {

            log.debug("Receive " + message.getChannel() + " from " + message.getSourceIdentity());

            final MatcherResult result = notificationMatcherService.findMatches(message);

            if (!result.hasMatches()) {
                log.debug("Unmatched incoming message '%s', attempting to identify workflow via contact lookup",
                        message.getSourceIdentity());
            }

            if (result.isMatchUnambiguous()) {
                final MatcherResultItem mostLikelyMatch = result.getMostLikelyMatch();

                if (mostLikelyMatch.getUser().getRole() == UserRole.Player) {
                    final PlayerResponse response = new PlayerResponse(
                            mostLikelyMatch.getMatch(),
                            (Player) mostLikelyMatch.getUser(),
                            getKind(mostLikelyMatch.getKind(), message.getBody()),
                            message.getBody());

                    notifyWorkflow(response);

                } else {
                    // implement team captain message processing
                }
            } else {
                // implement ambiguous processing
            }

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyWorkflow(PlayerResponse response) {
        final MatchWorkflow workflow = workflowService.getWorkflow(response.getMatch());
        if (workflow != null) {
            workflow.notify(response);
        }
    }

    private PlayerResponse.Kind getKind(NotificationKind kind, String data) {
        switch (kind) {
        case CanYouPlay:
        case Reminder:
            if (data.toLowerCase().contains("yes")) {
                return Kind.ICanPlay;
            }
            if (data.toLowerCase().contains("no")) {
                return Kind.ICantPlay;
            }
        case StandBy:
            if (data.toLowerCase().contains("yes")) {
                return Kind.ICanStandby;
            }
            if (data.toLowerCase().contains("no")) {
                return Kind.ICantPlay;
            }
        default:
            return Kind.Information;
        }
    }

}
