package com.okta.spring.example.models;

public class RegisteredUser {

    private String id;
    private String firstName;
    private String lastName;
    private boolean marketingOptOut;
    private String country;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isMarketingOptOut() {
        return marketingOptOut;
    }

    public void setMarketingOptOut(boolean marketingOptOut) {
        this.marketingOptOut = marketingOptOut;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isProfileComplete(){
        if(firstName == null || firstName.isEmpty() ||
        lastName == null || lastName.isEmpty() || country ==null){
            return false;
        }
        else{
            return true;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
