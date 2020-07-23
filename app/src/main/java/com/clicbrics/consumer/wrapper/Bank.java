package com.clicbrics.consumer.wrapper;

import com.buy.housing.backend.propertyEndPoint.model.Contact;
import com.buy.housing.backend.propertyEndPoint.model.KeyValuePair;

import java.util.List;

/**
 * Created by Alok on 07-04-2017.
 */

public class Bank {

    private List<Contact> contactList;
    private Long id;
    private Float interest;
    private String interestType;
    private String logo;
    private String name;
    private List<KeyValuePair> offersList;

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getInterest() {
        return interest;
    }

    public void setInterest(Float interest) {
        this.interest = interest;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<KeyValuePair> getOffersList() {
        return offersList;
    }

    public void setOffersList(List<KeyValuePair> offersList) {
        this.offersList = offersList;
    }
}
