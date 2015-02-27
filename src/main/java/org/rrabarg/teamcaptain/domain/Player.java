package org.rrabarg.teamcaptain.domain;

import java.util.Objects;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Player {

    private final ContactDetail contactDetail;
    private final Gender gender;
    private String id;

    public Player(String firstname, String surname, Gender gender, String emailAddress, String mobileNumber) {
        this(null, firstname, surname, gender, emailAddress, mobileNumber);
    }

    public Player(String id, String firstname, String surname, Gender gender, String emailAddress, String mobileNumber) {
        this.id = id;
        this.contactDetail = new ContactDetail(firstname, surname, emailAddress, mobileNumber);
        this.gender = gender;
    }

    public String getFirstname() {
        return contactDetail.getFirstname();
    }

    public String getSurname() {
        return contactDetail.getSurname();
    }

    public Gender getGender() {
        return gender;
    }

    public String getEmailAddress() {
        return contactDetail.getEmailAddress();
    }

    public String getMobileNumber() {
        return contactDetail.getMobileNumber();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public String getKey() {
        return getFirstname() + " " + getSurname();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

}
