package org.rrabarg.teamcaptain.domain;

import org.rrabarg.teamcaptain.strategy.ContactPreference;

public class Player extends User {

    public Player(String firstname, String surname, Gender gender, String emailAddress, String mobileNumber,
            ContactPreference contactPreference) {
        super(new ContactDetail(firstname, surname, emailAddress, mobileNumber), contactPreference, gender,
                UserRole.Player);
    }

    public Player(String firstname, String surname, Gender gender, String emailAddress, String mobileNumber) {
        super(new ContactDetail(firstname, surname, emailAddress, mobileNumber), gender, UserRole.Player);
    }

    public Player(String id, String firstname, String surname, Gender gender, String emailAddress, String mobileNumber,
            ContactPreference contactPreference) {
        super(id, new ContactDetail(firstname, surname, emailAddress, mobileNumber), gender, UserRole.Player);
    }
}
