package org.rrabarg.teamcaptain.channel.renderer;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;

class TextContentBuilder {

    private final Notification notification;

    TextContentBuilder(Notification notification) {
        this.notification = notification;
    }

    StringBuilder builder = new StringBuilder();

    public TextContentBuilder add(String s) {
        builder.append(s);
        return this;
    }

    public TextContentBuilder matchConfirmation() {
        this
                .hello()
                .append("Team selection has been now been confirmed.").space()
                .matchDetails()
                .teamForMatch()
                .travelDetails()
                .signoff();
        return this;
    }

    public TextContentBuilder space() {
        return append("  ");
    }

    private TextContentBuilder travelDetails() {
        return append("Travel details : ")
                .append(getMatch().getTravelDetails()).space();
    }

    private TextContentBuilder matchDetails() {
        return this.append("Location: ")
                .append(getMatch().getLocation().toString())
                .append(". When: ")
                .matchStartTime()
                .space();
    }

    private TextContentBuilder teamForMatch() {
        return append("Team: ")
                .append(getMatch().getAcceptedPlayers(notification.getPlayerPool()).toString())
                .space();
    }

    private TextContentBuilder matchStartTime() {
        return this.append(getMatch().getStartDateTime()
                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
    }

    public TextContentBuilder canYouStandby() {
        this
                .hello()
                .append("Can you standby for this match?")
                .space()
                .matchDetails()
                .answerYesOrNo()
                .signoff();
        return this;
    }

    public TextContentBuilder confirmStandby() {
        this
                .hello()
                .append("Brilliant, thanks, I'll be in touch shortly to confirm. ").space()
                .signoff();
        return this;
    }

    public TextContentBuilder append(String s) {
        if (s != null) {
            builder.append(s);
        }
        return this;
    }

    public String build() {
        return builder.toString();
    }

    public TextContentBuilder matchTitle() {
        builder.append(getMatch().getTitle());
        return this;
    }

    private Match getMatch() {
        return notification.getMatch();
    }

    public TextContentBuilder reminder() {
        this
                .hello()
                .append("Match reminder!!").space()
                .answerYesOrNo()
                .signoff();
        return this;
    }

    public TextContentBuilder canYouPlayContent() {
        this
                .hello()
                .append("You been selected to play!").space()
                .matchDetails()
                .answerYesOrNo()
                .signoff();
        return this;
    }

    public TextContentBuilder confirmAcceptance() {
        this
                .hello()
                .append("Brilliant, your in. I'll be in touch shortly with details.")
                .space()
                .signoff();
        return this;
    }

    public TextContentBuilder confirmDecline() {
        this.hello()
                .append("Sorry you couldn't play, hope too see you soon.")
                .space()
                .signoff();
        return this;
    }

    private TextContentBuilder matchDate() {
        this.append(getMatch().getStartDateTime()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        return this;
    }

    public TextContentBuilder signoff() {
        return append("Thanks ")
                .append(notification.getTeamCaptain().getFirstname());
    }

    public TextContentBuilder answerYesOrNo() {
        return append("Please reply with the text YES or NO.").space();
    }

    public TextContentBuilder hello() {
        return append("Hi ")
                .append(notification.getTargetContactDetail().getFirstname())
                .append(",")
                .space();
    }

    public TextContentBuilder matchStatus() {
        this
                .hello()
                .append("Unfortunately a full team has not not yet been selected, here is a status update.")
                .space()
                .playerStatus()
                .signoff();
        return this;
    }

    private TextContentBuilder playerStatus() {
        final List<Player> acceptedPlayers = getMatch().getAcceptedPlayers(notification.getPlayerPool());
        final List<Player> declinedPlayers = getMatch().getDeclinedPlayers(notification.getPlayerPool());
        final List<Player> notifiedPlayers = getMatch().getNotifiedPlayers(notification.getPlayerPool());
        final List<Player> onStandbyPlayers = getMatch().getAcceptedOnStandbyPlayers(
                notification.getPlayerPool());

        return playerStatusDetail(acceptedPlayers, "accepted")
                .playerStatusDetail(declinedPlayers, "declined")
                .playerStatusDetail(notifiedPlayers, "yet to respond")
                .playerStatusDetail(onStandbyPlayers, "accepted a standby request");
    }

    private TextContentBuilder playerStatusDetail(List<Player> player, String description) {
        if (player.isEmpty()) {
            this.append("No players have ").append(description).space();
        } else {
            this.append("The following players have ").append(description).append(" : ");
            this.append(player.toString());
            this.space();
        }
        return this;
    }

    public TextContentBuilder alert(NotificationKind kind) {
        this
                .hello()
                .append("Alert : " + kind)
                .playerStatus()
                .signoff();
        return this;
    }

    public TextContentBuilder standdown() {
        this
                .hello()
                .append("Thankyou for standing by for this match, we've managed to get full team now, so won't need you for this match after all.")
                .space()
                .signoff();
        return this;
    }

    public TextContentBuilder matchTitleAndDate() {
        return matchTitle().append(" on ").matchDate();
    }

}