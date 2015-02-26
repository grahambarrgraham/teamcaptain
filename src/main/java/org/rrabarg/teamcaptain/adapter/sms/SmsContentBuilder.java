package org.rrabarg.teamcaptain.adapter.sms;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Notification;

class SmsContentBuilder {

     private final Notification notification;

    SmsContentBuilder(Notification notification) {
        this.notification = notification;
    }

    StringBuilder builder = new StringBuilder();

    public SmsContentBuilder add(String s) {
        builder.append(s);
        return this;
    }

    public SmsContentBuilder matchConfirmation() {
        this
                .hello()
                .matchDescription()
                .matchDetails()
                .travelDetails()
                .signoff();
        return this;
    }

    private SmsContentBuilder travelDetails() {
        return this.append(getMatch().getTravelDetails());
    }

    private SmsContentBuilder matchDetails() {
        this.append("Location: ")
                .append(getMatch().getLocation().toString())
                .append(". When: ")
                .matchStartTime()
                .append(" on ")
                .matchDate()
                .append(". ")
                .teamForMatch()
                .append(". ");
        return this;
    }

    private SmsContentBuilder teamForMatch() {
        this.append("Team: ");
        this.append(getMatch().getAcceptedPlayers(notification.getPlayerPool()).toString());
        return this;
    }

    private SmsContentBuilder matchStartTime() {
        return this.append(getMatch().getStartDateTime()
                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
    }

    public SmsContentBuilder canYouStandby() {
        this
                .hello()
                .append("Can you standby for ")
                .matchDescription()
                .answerYesOrNo()
                .signoff();
        return this;
    }

    public SmsContentBuilder confirmStandby() {
        this
                .hello()
                .append("Brilliant, thanks, I'll be in touch shortly to confirm. ")
                .signoff();
        return this;
    }

    public SmsContentBuilder append(String s) {
        if (s != null) {
            builder.append(s);
        }
        return this;
    }

    public String build() {
        return builder.toString();
    }

    public SmsContentBuilder matchTitle() {
        builder.append(getMatch().getTitle());
        return this;
    }

    private Match getMatch() {
        return notification.getMatch();
    }

    public SmsContentBuilder reminder() {
        this
                .hello()
                .append("Reminder. ")
                .matchDescription()
                .answerYesOrNo()
                .signoff();
        return this;
    }

    public SmsContentBuilder matchDescription() {
        this.append(" Match : ")
                .matchTitle()
                .append(" on ")
                .matchDate()
                .append(" at ")
                .matchStartTime()
                .append(". ");
        return this;
    }

    public SmsContentBuilder canYouPlayContent() {
        this
                .hello()
                .append("Can you play ")
                .matchDescription()
                .answerYesOrNo()
                .signoff();
        return this;
    }

    public SmsContentBuilder confirmAcceptance() {
        this
                .hello()
                .append("Brilliant, your in. I'll be in touch shortly with details. ")
                .signoff();
        return this;
    }

    public SmsContentBuilder confirmDecline() {
        this.hello()
                .append("Sorry you couldn't play, hope too see you soon. ")
                .signoff();
        return this;
    }

    private SmsContentBuilder matchDate() {
        this.append(getMatch().getStartDateTime()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        return this;
    }

    public SmsContentBuilder signoff() {
        this
                .append("Thanks ")
                .append(notification.getOrganiserFirstName());
        return this;
    }

    public SmsContentBuilder answerYesOrNo() {
        this
                .append("Please reply with the text YES or NO. ");
        return this;
    }

    public SmsContentBuilder hello() {
        this
                .append("Hi ")
                .append(notification.getPlayer().getFirstname())
                .append(". ");
        return this;
    }

}