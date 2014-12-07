package org.rrabarg.teamcaptain.domain;

public class Location {
    private final String firstLine;
    private final String postcode;

    public Location(String firstLine, String postcode) {
        this.firstLine = firstLine;
        this.postcode = postcode;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public String toString() {
        return getFirstLine() + ", " + getPostcode();
    }

    public static Location fromString(String locationString) {
        final String[] split = locationString.split(",");
        return new Location(split[0].trim(), split[1].trim());
    }
}
