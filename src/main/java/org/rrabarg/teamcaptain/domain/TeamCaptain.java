package org.rrabarg.teamcaptain.domain;

public class TeamCaptain {

    private String id;

    private final ContactDetail contactDetail;

    public TeamCaptain(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public String getFirstName() {
        return contactDetail.getFirstname();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
