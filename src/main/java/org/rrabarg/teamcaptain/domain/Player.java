package org.rrabarg.teamcaptain.domain;

public class Player {

    String firstname;
    String surname;
    Gender gender;

    public Player(String firstname, String surname, Gender gender) {
        this.firstname = firstname;
        this.surname = surname;
        this.gender = gender;
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

}
