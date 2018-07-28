package uk.co.novinet.service;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class Claim {
    private Long id;
    private Long userId;
    private String claimToken;
    private String title;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postcode;
    private String country;
    private String phoneNumber;
    private String canShowWrittenEvidence;
    private String schemeDetails;
    private String schemeAdvisorDetails;
    private String additionalInformation;

    public Claim(
            Long id,
            Long userId,
            String claimToken,
            String title,
            String firstName,
            String lastName,
            String emailAddress,
            String addressLine1,
            String addressLine2,
            String city,
            String postcode,
            String country,
            String phoneNumber,
            String canShowWrittenEvidence,
            String schemeDetails,
            String schemeAdvisorDetails,
            String additionalInformation) {
        this.id = id;
        this.userId = userId;
        this.claimToken = claimToken;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.canShowWrittenEvidence = canShowWrittenEvidence;
        this.schemeDetails = schemeDetails;
        this.schemeAdvisorDetails = schemeAdvisorDetails;
        this.additionalInformation = additionalInformation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimToken() {
        return claimToken;
    }

    public void setClaimToken(String claimToken) {
        this.claimToken = claimToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCanShowWrittenEvidence() {
        return canShowWrittenEvidence;
    }

    public void setCanShowWrittenEvidence(String canShowWrittenEvidence) {
        this.canShowWrittenEvidence = canShowWrittenEvidence;
    }

    public String getSchemeDetails() {
        return schemeDetails;
    }

    public void setSchemeDetails(String schemeDetails) {
        this.schemeDetails = schemeDetails;
    }

    public String getSchemeAdvisorDetails() {
        return schemeAdvisorDetails;
    }

    public void setSchemeAdvisorDetails(String schemeAdvisorDetails) {
        this.schemeAdvisorDetails = schemeAdvisorDetails;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
