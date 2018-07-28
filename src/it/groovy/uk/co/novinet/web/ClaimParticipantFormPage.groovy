package uk.co.novinet.web

import geb.Page

class ClaimParticipantFormPage extends Page {

    static url = "http://localhost:8484"

    static at = { title == "Loan Charge Action Group | Claim Participant Form" }

    static content = {
        titleInput { $("#title") }
        firstNameInput { $("#firstName") }
        lastNameInput { $("#lastName") }
        emailAddressInput { $("#emailAddress") }
        addressLine1Input { $("#addressLine1") }
        addressLine2Input { $("#addressLine2") }
        cityInput { $("#city") }
        postcodeInput { $("#postcode") }
        phoneNumberInput { $("#phoneNumber") }
        countryInput { $("#country") }
        canShowWrittenEvidenceInput { $("input[name=canShowWrittenEvidence]") }
        schemeDetailsInput { $("#schemeDetails") }
        schemeAdvisorDetailsInput { $("#schemeAdvisorDetails") }
        additionalInformationInput { $("#additionalInformation") }

        //errors
        titleError { $("#title-error") }
        firstNameError { $("#firstName-error") }
        lastNameError { $("#lastName-error") }
        emailAddressError { $("#emailAddress-error") }
        addressLine1Error { $("#addressLine1-error") }
        addressLine2Error(wait: false, required: false) { $("#addressLine2-error") }
        cityError { $("#city-error") }
        postcodeError { $("#postcode-error") }
        phoneNumberError { $("#phoneNumber-error") }
        countryError { $("#country-error") }
        canShowWrittenEvidenceError { $("#canShowWrittenEvidence-error") }
        schemeDetailsError { $("#schemeDetails-error") }
        schemeAdvisorDetailsError { $("#schemeAdvisorDetails-error") }
        additionalInformationError(wait: false, required: false) { $("#additionalInformation-error") }

        submitButton { $("#uploadButton") }
    }
}
