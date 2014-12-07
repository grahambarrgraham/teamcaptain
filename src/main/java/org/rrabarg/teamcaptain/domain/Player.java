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
        return getKey();
    }

    public String getKey() {
        return getFirstname() + " " + getSurname();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((firstname == null) ? 0 : firstname.hashCode());
        result = (prime * result) + ((surname == null) ? 0 : surname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (firstname == null) {
            if (other.firstname != null) {
                return false;
            }
        } else if (!firstname.equals(other.firstname)) {
            return false;
        }
        if (surname == null) {
            if (other.surname != null) {
                return false;
            }
        } else if (!surname.equals(other.surname)) {
            return false;
        }
        return true;
    }

}
