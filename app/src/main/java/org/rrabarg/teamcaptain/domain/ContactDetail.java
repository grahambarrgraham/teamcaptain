package org.rrabarg.teamcaptain.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ContactDetail {

    private final String firstname;
    private final String surname;
    private final String emailAddress;
    private final String mobileNumber;

    public ContactDetail(String firstname, String surname, String emailAddress, String mobileNumber) {
        this.firstname = firstname;
        this.surname = surname;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public String getIdentity(Channel channel) {
        switch (channel) {
        case Email:
            return emailAddress;
        case Sms:
            return mobileNumber;
        default:
            break;
        }
        return null;
    }

}
