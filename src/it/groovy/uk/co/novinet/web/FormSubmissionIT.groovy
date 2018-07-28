package uk.co.novinet.web

import geb.spock.GebSpec
import org.apache.commons.io.FileUtils
import uk.co.novinet.e2e.SftpDocument

import static uk.co.novinet.e2e.TestUtils.*

class FormSubmissionIT extends GebSpec {

    static final schemes = "schemes"

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
            assert getClaimRows().get(0).title == "title"
            assert getClaimRows().get(0).firstName == "firstName"
            assert getClaimRows().get(0).lastName == "lastName"
            assert getClaimRows().get(0).emailAddress == "email@address.com"
            assert getClaimRows().get(0).addressLine1 == "addressLine1"
            assert getClaimRows().get(0).addressLine2 == "addressLine2"
            assert getClaimRows().get(0).city == "city"
            assert getClaimRows().get(0).postcode == "postcode"
            assert getClaimRows().get(0).country == "country"
            assert getClaimRows().get(0).phoneNumber == "phoneNumber"
            assert getClaimRows().get(0).canShowWrittenEvidence == "yes"
            assert getClaimRows().get(0).schemeDetails == "schemeDetails"
            assert getClaimRows().get(0).schemeAdvisorDetails == "schemeAdvisorDetails"
            assert getClaimRows().get(0).additionalInformation == "additionalInformation"
            assert getUserRows().get(0).hasCompletedClaimParticipantForm == true
            assert getUserRows().get(0).hasBeenSentClaimConfirmationEmail == true
    }

    def "landing page is thank you page when already submitted"() {
        given:
            assert getClaimRows().size() == 0
            runSqlUpdate("update `i7b0_users` set " +
                    "has_completed_claim_participant_form = '" + 1 + "' " +
                    "where uid = 1"
            )

        when:
            go "http://localhost:8484?token=claim_1"
            waitFor { at ThankYouPage }

        then:
            at ThankYouPage
    }

    def "landing page is claim participant form page when has submitted membership form"() {
        given:
            assert getClaimRows().size() == 0
            runSqlUpdate("update `i7b0_users` set " +
                    "has_completed_membership_form = '" + 1 + "' " +
                    "where uid = 1"
            )

        when:
            go "http://localhost:8484?token=claim_1"
            waitFor { at ClaimParticipantFormPage }

        then:
            at ClaimParticipantFormPage
    }

    private prepopulateMemberDataInDb() {
        runSqlUpdate("update `i7b0_users` set " +
                "schemes = '" + schemes + "' " +
                "where uid = 1"
        )
    }
}
