package org.rrabarg.teamcaptain.domain;

import org.rrabarg.teamcaptain.strategy.ContactPreference;

public class TeamCaptain extends User {

    public TeamCaptain(ContactDetail contactDetail, Gender gender) {
        super(contactDetail, gender, UserRole.TeamCaptain);
    }

    public TeamCaptain(ContactDetail contactDetail, ContactPreference contactPreference, Gender gender) {
        super(contactDetail, contactPreference, gender, UserRole.TeamCaptain);
    }

}
