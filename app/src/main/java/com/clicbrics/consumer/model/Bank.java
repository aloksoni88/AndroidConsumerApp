package com.clicbrics.consumer.model;


import com.buy.housing.backend.propertyEndPoint.model.KeyValuePair;

import java.util.List;

/**
 * Created by mehar on 15-09-2016.
 */
public class Bank {

    private Long id;
//    @Index
    private String name;
    private String logo;
    private Float interest;
    private List<Contact> contactList;
    private String searchNameInUp;
    private List<KeyValuePair> offersList;

    private String bankCode;
    private Float commission;
    private List<Document> mouDocs;
    private Long mouExpiry;

    public Bank() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Float getInterest() {
        return interest;
    }

    public void setInterest(Float interest) {
        this.interest = interest;
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public String getSearchNameInUp() {
        return searchNameInUp;
    }

    public void setSearchNameInUp(String searchNameInUp) {
        this.searchNameInUp = searchNameInUp;
    }

    public List<KeyValuePair> getOffersList() {
        return offersList;
    }

    public void setOffersList(List<KeyValuePair> offersList) {
        this.offersList = offersList;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Float getCommission() {
        return commission;
    }

    public void setCommission(Float commission) {
        this.commission = commission;
    }

    public List<Document> getMouDocs() {
        return mouDocs;
    }

    public void setMouDocs(List<Document> mouDocs) {
        this.mouDocs = mouDocs;
    }

    public Long getMouExpiry() {
        return mouExpiry;
    }

    public void setMouExpiry(Long mouExpiry) {
        this.mouExpiry = mouExpiry;
    }
}
