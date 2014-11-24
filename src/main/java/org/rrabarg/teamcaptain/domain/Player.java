package org.rrabarg.teamcaptain.domain;

public class Player {

    private final String firstname;
    private final String surname;
    private final Gender gender;
    private final String emailAddress;
    private final String mobileNumber;
    private String id;

    public Player(String firstname, String surname, Gender gender, String emailAddress, String mobileNumber) {
        this(null, firstname, surname, gender, emailAddress, mobileNumber);
    }

    public Player(String id, String firstname, String surname, Gender gender, String emailAddress, String mobileNumber) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.gender = gender;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public Gender getGender() {
        return gender;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getFirstname() + " " + getSurname() + " id:(" + getId() + ")";
    }

}
