package org.rrabarg.teamcaptain.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.rrabarg.teamcaptain.strategy.ContactPreference;

public class User {

    private static final ContactPreference DEFAULT_CONTACT_PREFERENCE = ContactPreference
            .emailOnly();

    private final ContactPreference contactPreference;
    private final ContactDetail contactDetail;
    private final Gender gender;
    private String id;
    private final UserRole role;

    public enum UserRole {
        TeamCaptain, Player;
    }

    public User(ContactDetail contactDetail, ContactPreference contactPreference, Gender gender, UserRole role) {
        this(null, contactDetail, contactPreference, gender, role);
    }

    public User(ContactDetail contactDetail, Gender gender, UserRole userRole) {
        this(null, contactDetail, DEFAULT_CONTACT_PREFERENCE, gender, userRole);
    }

    public User(String id, ContactDetail contactDetail, Gender gender, UserRole userRole) {
        this(id, contactDetail, DEFAULT_CONTACT_PREFERENCE, gender, userRole);
    }

    public User(String id, ContactDetail contactDetail, ContactPreference contactPreference, Gender gender,
            UserRole role) {
        this.id = id;
        this.contactDetail = contactDetail;
        this.contactPreference = contactPreference;
        this.gender = gender;
        this.role = role;
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

    public UserRole getRole() {
        return role;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public ContactPreference getContactPreference() {
        return contactPreference;
    };
}
