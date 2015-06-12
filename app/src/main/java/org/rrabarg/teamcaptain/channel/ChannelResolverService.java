package org.rrabarg.teamcaptain.channel;

import java.util.Set;

import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.strategy.ContactPreference;
import org.springframework.stereotype.Component;

@Component
public class ChannelResolverService {

    public Set<Channel> getChannels(Notification notification) {
        final ContactPreference preference = resolve(
                notification.getCompetitionContactPreference(),
                notification.getTargetContactPreference());

        switch (notification.getCategory()) {
        case Question:
            return preference.getPreferredQuestionChannels();
        case Information:
        default:
            return preference.getPreferredInformationBroadcastChannels();
        }

    }

    private ContactPreference resolve(ContactPreference competitionContactPreference,
            ContactPreference targetContactPreference) {
        return targetContactPreference; // TODO take account of competition preferences
    }
}
