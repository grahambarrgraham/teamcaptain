package org.rrabarg.teamcaptain.strategy;

import java.util.EnumSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rrabarg.teamcaptain.domain.Channel;

public class ContactPreference {

    private final Set<Channel> preferredInformationBroadcastChannels;
    private final Set<Channel> preferredQuestionChannels;

    public ContactPreference(
            @JsonProperty("preferredInformationBroadcastChannels") Set<Channel> preferredInformationBroadcastChannels,
            @JsonProperty("preferredQuestionChannels") Set<Channel> preferredQuestionChannels) {
        this.preferredInformationBroadcastChannels = preferredInformationBroadcastChannels;
        this.preferredQuestionChannels = preferredQuestionChannels;
    }

    public Set<Channel> getPreferredInformationBroadcastChannels() {
        return preferredInformationBroadcastChannels;
    }

    public Set<Channel> getPreferredQuestionChannels() {
        return preferredQuestionChannels;
    }

    public static ContactPreference smsOnly() {
        return new ContactPreference(EnumSet.of(Channel.Sms), EnumSet.of(Channel.Sms));
    }

    public static ContactPreference emailOnly() {
        return new ContactPreference(EnumSet.of(Channel.Email), EnumSet.of(Channel.Email));
    }

    public static ContactPreference smsWithEmailBroadcast() {
        return new ContactPreference(EnumSet.of(Channel.Email), EnumSet.of(Channel.Email, Channel.Sms));
    }

    public static ContactPreference smsQuestionsWithEmailBroadcast() {
        return new ContactPreference(EnumSet.of(Channel.Email), EnumSet.of(Channel.Sms));
    }

}