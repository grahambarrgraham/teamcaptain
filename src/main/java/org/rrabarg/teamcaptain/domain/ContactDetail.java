package org.rrabarg.teamcaptain.domain;

import java.util.Objects;

import org.apache.commons.lang.builder.EqualsBuilder;

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
        return Objects.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
