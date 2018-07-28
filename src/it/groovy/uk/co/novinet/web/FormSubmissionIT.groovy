package uk.co.novinet.web

import geb.spock.GebSpec
import org.apache.commons.io.FileUtils
import uk.co.novinet.e2e.SftpDocument

import static uk.co.novinet.e2e.TestUtils.*

class FormSubmissionIT extends GebSpec {

    static final mpName = "mpName"
    static final mpConstituency = "mpConstituency"
    static final mpParty = "mpParty"
    static final mpEngaged = 1
    static final mpSympathetic = 1
    static final schemes = "schemes"
    static final industry = "industry"
    static final howDidYouHearAboutLcag = "howDidYouHearAboutLcag"
    static final memberOfBigGroup = 1
    static final bigGroupUsername = "bigGroupUsername"

    def setup() {
        setupDatabaseSchema()
    }

    def "claim participant form is pre-populated with member email address"() {
        given:
            prepopulateMemberDataInDb()
            go "http://localhost:8484?token=claim_1"

        when:
            waitFor { at ClaimParticipantFormPage }

        then:
            waitFor { emailAddressInput.value() == "user1@something.com" }
    }

    def "claim participant form cannot be submitted when fields are blank"() {
        given:
            go "http://localhost:8484?token=claim_1"
            waitFor { at ClaimParticipantFormPage }
            firstNameInput.value("")
            lastNameInput.value("")
            emailAddressInput.value("")

        when:
            submitButton.click()

        then:
            waitFor { at ClaimParticipantFormPage }
            waitFor { titleError.displayed }
            assert titleError.displayed == true
            assert firstNameError.displayed == true
            assert lastNameError.displayed == true
            assert emailAddressError.displayed == true
            assert addressLine1Error.displayed == true
            assert addressLine2Error.present == false
            assert cityError.displayed == true
            assert postcodeError.displayed == true
            assert phoneNumberError.displayed == true
            assert countryError.displayed == true
            assert canShowWrittenEvidenceError.displayed == true
            assert schemeDetailsError.displayed == true
            assert schemeAdvisorDetailsError.displayed == true
            assert additionalInformationError.present == false
    }

    def "claim participant form can be submitted when all mandatory fields are supplied"() {
        given:
        assert getClaimRows().size() == 0
        go "http://localhost:8484?token=claim_1"
        waitFor { at ClaimParticipantFormPage }
        titleInput.value("title")
        firstNameInput.value("firstName")
        lastNameInput.value("lastName")
        emailAddressInput.value("email@address.com")
        addressLine1Input.value("addressLine1")
        addressLine2Input.value("addressLine2")
        cityInput.value("city")
        postcodeInput.value("postcode")
        countryInput.value("country")
        phoneNumberInput.value("phoneNumber")
        canShowWrittenEvidenceYes.click()
        schemeDetailsInput.value("schemeDetails")
        schemeAdvisorDetailsInput.value("schemeAdvisorDetails")
        additionalInformationInput.value("additionalInformation")

        when:
        submitButton.click()

        then:
        waitFor { at ThankYouPage }
        def member = getUserRows().get(0)
        def claim = getClaimRows().get(0)
        assert claim.title == "title"
        assert claim.firstName == "firstName"
        assert claim.lastName == "lastName"
        assert claim.emailAddress == "email@address.com"
        assert claim.addressLine1 == "addressLine1"
        assert claim.addressLine2 == "addressLine2"
        assert claim.city == "city"
        assert claim.postcode == "postcode"
        assert claim.country == "country"
        assert claim.phoneNumber == "phoneNumber"
        assert claim.canShowWrittenEvidence == "yes"
        assert claim.schemeDetails == "schemeDetails"
        assert claim.schemeAdvisorDetails == "schemeAdvisorDetails"
        assert claim.additionalInformation == "additionalInformation"
    }

}
