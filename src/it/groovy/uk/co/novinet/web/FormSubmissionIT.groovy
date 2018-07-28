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

    def "big group username field is displayed when i say i am a member of big group"() {
        given:
            go "http://localhost:8484?token=1234_1"
            waitFor { at ClaimParticipantFormPage }
            nameInput.value("")
            emailAddressInput.value("")
            assert bigGroupUsernameInput.displayed == false
            assert identificationInput.displayed == false
            assert proofOfSchemeInvolvementInput.displayed == false
            assert documentUploadErrorBanner.displayed == false

        when:
            memberOfBigGroupInput.value(true)

        then:
            waitFor { bigGroupUsernameInput.displayed }
            assert documentUploadErrorBanner.displayed == false
            assert bigGroupUsernameInput.displayed == true
            assert identificationInput.displayed == false
            assert proofOfSchemeInvolvementInput.displayed == false

        when: "i try to submit the form"
            submitButton.click()

        then: "all previous errors are displayed as well as the big group username validation error"
            waitFor { at ClaimParticipantFormPage }
            waitFor { nameInputError.displayed }
            assert documentUploadErrorBanner.displayed == false
            assert nameInputError.displayed
            assert emailAddressError.displayed
            assert mpNameError.displayed
            assert mpPartyError.displayed
            assert mpConstituencyError.displayed
            assert mpEngagedError.displayed
            assert mpSympatheticError.displayed
            assert schemesError.displayed
            assert industryError.displayed
            assert howDidYouHearAboutLcagError.displayed
            assert memberOfBigGroupError.displayed == false
            assert bigGroupUsernameError.displayed
    }

    def "id and scheme doc upload fields are displayed when i say i am not a member of big group"() {
        given:
            go "http://localhost:8484?token=1234_1"
            waitFor { at ClaimParticipantFormPage }
            nameInput.value("")
            emailAddressInput.value("")
            assert bigGroupUsernameInput.displayed == false
            assert identificationInput.displayed == false
            assert proofOfSchemeInvolvementInput.displayed == false
            assert documentUploadErrorBanner.displayed == false

        when:
            memberOfBigGroupInput.value(false)

        then:
            waitFor { identificationInput.displayed }
            waitFor { proofOfSchemeInvolvementInput.displayed }
            assert documentUploadErrorBanner.displayed == false
            assert bigGroupUsernameInput.displayed == false
            assert identificationInput.displayed == true
            assert proofOfSchemeInvolvementInput.displayed == true

        when: "i try to submit the form"
            submitButton.click()

        then: "all previous errors are displayed as well as the big group username validation error"
            waitFor { at ClaimParticipantFormPage }
            waitFor { nameInputError.displayed }
            assert documentUploadErrorBanner.displayed == false
            assert nameInputError.displayed
            assert emailAddressError.displayed
            assert mpNameError.displayed
            assert mpPartyError.displayed
            assert mpConstituencyError.displayed
            assert mpEngagedError.displayed
            assert mpSympatheticError.displayed
            assert schemesError.displayed
            assert industryError.displayed
            assert howDidYouHearAboutLcagError.displayed
            assert $("#bigGroupUsername-error").displayed == false
            assert identificationError.displayed
            assert proofOfSchemeInvolvementError.displayed
    }

    def "can complete membership form as existing big group user"() {
        given:
            go "http://localhost:8484?token=1234_1"
            waitFor { at ClaimParticipantFormPage }
            assert documentUploadErrorBanner.displayed == false

        when:
            nameInput.value("john smith")
            emailAddressInput.value("test@test.com")
            mpNameInput.value("some mp")
            mpPartyInput.value("conservative")
            mpConstituencyInput.value("some constituency")
            mpEngagedInput.value(true)
            mpSympatheticInput.value(true)
            schemesInput.value("scheme 1 and scheme 2")
            industryInput.value("some industry")
            howDidYouHearAboutLcagInput.value("from contractor uk forum")
            memberOfBigGroupInput.value(true)
            bigGroupUsernameInput.value("bigGroupUsername")
            submitButton.click()

        then: "all previous errors are displayed as well as the big group username validation error"
            waitFor { at ThankYouPage }
            def member = getUserRows().get(0)
            assert member.name == "john smith"
            assert member.emailAddress == "test@test.com"
            assert member.mpName == "some mp"
            assert member.mpParty == "conservative"
            assert member.mpConstituency == "some constituency"
            assert member.mpEngaged == true
            assert member.mpSympathetic == true
            assert member.schemes == "scheme 1 and scheme 2"
            assert member.industry == "some industry"
            assert member.howDidYouHearAboutLcag == "from contractor uk forum"
            assert member.memberOfBigGroup == true
            assert member.bigGroupUsername == "bigGroupUsername"
    }

    def "can complete membership form with id and proof of scheme docs"() {
        given:
            go "http://localhost:8484?token=1234_1"
            waitFor { at ClaimParticipantFormPage }
            File idendificationFile = createFileWithSize(100)
            File proofOfSchemeInvolvementFile = createFileWithSize(100)
            assert documentUploadErrorBanner.displayed == false

        when:
            nameInput.value("john smith")
            emailAddressInput.value("test@test.com")
            mpNameInput.value("some mp")
            mpPartyInput.value("conservative")
            mpConstituencyInput.value("some constituency")
            mpEngagedInput.value(true)
            mpSympatheticInput.value(true)
            schemesInput.value("scheme 1 and scheme 2")
            industryInput.value("some industry")
            howDidYouHearAboutLcagInput.value("from contractor uk forum")
            memberOfBigGroupInput.value(false)
            identificationInput.value(idendificationFile.getAbsolutePath())
            proofOfSchemeInvolvementInput.value(proofOfSchemeInvolvementFile.getAbsolutePath())
            submitButton.click()

        then:
            waitFor { at ThankYouPage }
            def member = getUserRows().get(0)
            assert member.name == "john smith"
            assert member.emailAddress == "test@test.com"
            assert member.mpName == "some mp"
            assert member.mpParty == "conservative"
            assert member.mpConstituency == "some constituency"
            assert member.mpEngaged == true
            assert member.mpSympathetic == true
            assert member.schemes == "scheme 1 and scheme 2"
            assert member.industry == "some industry"
            assert member.howDidYouHearAboutLcag == "from contractor uk forum"
            assert member.memberOfBigGroup == false
            assert member.bigGroupUsername == ""
    }

    def "trying to submit zero byte docs results in validation error"() {
        given:
            go "http://localhost:8484?token=1234_1"
            waitFor { at ClaimParticipantFormPage }
            File zeroByteFile = createFileWithSize(0)
            assert documentUploadErrorBanner.displayed == false

        when:
            nameInput.value("john smith")
            emailAddressInput.value("test@test.com")
            mpNameInput.value("some mp")
            mpPartyInput.value("conservative")
            mpConstituencyInput.value("some constituency")
            mpEngagedInput.value(true)
            mpSympatheticInput.value(true)
            schemesInput.value("scheme 1 and scheme 2")
            industryInput.value("some industry")
            howDidYouHearAboutLcagInput.value("from contractor uk forum")
            memberOfBigGroupInput.value(false)
            identificationInput.value(zeroByteFile.getAbsolutePath())
            proofOfSchemeInvolvementInput.value(zeroByteFile.getAbsolutePath())
            submitButton.click()

        then:
            waitFor { at ClaimParticipantFormPage }
            def member = getUserRows().get(0)
            waitFor { documentUploadErrorBanner.displayed }
            assert documentUploadErrorBanner.displayed
            assert member.name == "john smith"
            assert member.emailAddress == "test@test.com"
            assert member.mpName == "some mp"
            assert member.mpParty == "conservative"
            assert member.mpConstituency == "some constituency"
            assert member.mpEngaged == true
            assert member.mpSympathetic == true
            assert member.schemes == "scheme 1 and scheme 2"
            assert member.industry == "some industry"
            assert member.howDidYouHearAboutLcag == "from contractor uk forum"
            assert member.memberOfBigGroup == false
            assert member.bigGroupUsername == ""

            nameInput.value() == "john smith"
            emailAddressInput.value() == "test@test.com"
            mpNameInput.value() == "some mp"
            mpPartyInput.value() == "conservative"
            mpConstituencyInput.value() == "some constituency"
            mpEngagedInput[0].value() == "true"
            mpSympatheticInput[0].value() == "true"
            schemesInput.value() == "scheme 1 and scheme 2"
            industryInput.value() == "some industry"
            howDidYouHearAboutLcagInput.value() == "from contractor uk forum"
            memberOfBigGroupInput[1].value() == "false"
            identificationInput.displayed == true
            proofOfSchemeInvolvementInput.displayed == true

        when:
            File idendificationFile = createFileWithSize(100)
            File proofOfSchemeInvolvementFile = createFileWithSize(100)

            identificationInput.value(idendificationFile.getAbsolutePath())
            proofOfSchemeInvolvementInput.value(proofOfSchemeInvolvementFile.getAbsolutePath())

            submitButton.click()
            waitFor { at ThankYouPage }
            member = getUserRows().get(0)

            sftpDocuments.sort(new Comparator<SftpDocument>() {
                @Override
                int compare(SftpDocument o1, SftpDocument o2) {
                    if (o1.size == o2.size) return 0
                    if (o1.size > o2.size) return -1
                    return 1
                }
            })

        then:
            assert member.name == "john smith"
            assert member.emailAddress == "test@test.com"
            assert member.mpName == "some mp"
            assert member.mpParty == "conservative"
            assert member.mpConstituency == "some constituency"
            assert member.mpEngaged == true
            assert member.mpSympathetic == true
            assert member.schemes == "scheme 1 and scheme 2"
            assert member.industry == "some industry"
            assert member.howDidYouHearAboutLcag == "from contractor uk forum"
            assert member.memberOfBigGroup == false
            assert member.bigGroupUsername == ""
    }

    private createFileWithSize(Long kb) {
        def file = File.createTempFile("lcag", "test")
        FileUtils.touch(file)
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")
        randomAccessFile.setLength(1024 * kb)
        return file
    }

    private prepopulateMemberDataInDb() {
        runSqlUpdate("update `i7b0_users` set " +
                "mp_name = '" + mpName + "', " +
                "mp_constituency = '" + mpConstituency + "', " +
                "mp_party = '" + mpParty + "', " +
                "mp_engaged = '" + mpEngaged + "', " +
                "mp_sympathetic = '" + mpSympathetic + "', " +
                "schemes = '" + schemes + "', " +
                "industry = '" + industry + "', " +
                "how_did_you_hear_about_lcag = '" + howDidYouHearAboutLcag + "', " +
                "member_of_big_group = '" + memberOfBigGroup + "', " +
                "big_group_username = '" + bigGroupUsername + "' " +
                "where uid = 1"
        )
    }
}
