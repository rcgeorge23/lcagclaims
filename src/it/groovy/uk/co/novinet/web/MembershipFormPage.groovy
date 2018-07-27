package uk.co.novinet.web

import geb.Page

class MembershipFormPage extends Page {

    static url = "http://localhost:8383"

    static at = { title == "Loan Charge Action Group | Membership Form" }

    static content = {
        nameInput { $("#name") }
        emailAddressInput { $("#emailAddress") }
        mpNameInput { $("#mpName") }
        mpPartyInput { $("#mpParty") }
        mpConstituencyInput { $("#mpConstituency") }
        mpEngagedInput { $("input[name=mpEngaged]") }
        mpSympatheticInput { $("input[name=mpSympathetic]") }
        schemesInput { $("#schemes") }
        industryInput { $("#industry") }
        howDidYouHearAboutLcagInput { $("#howDidYouHearAboutLcag") }
        memberOfBigGroupInput { $("input[name=memberOfBigGroup]") }
        bigGroupUsernameInput { $("#bigGroupUsername") }
        identificationInput { $("#identification") }
        proofOfSchemeInvolvementInput { $("#proofOfSchemeInvolvement") }

        //errors
        nameInputError { $("#name-error") }
        emailAddressError { $("#emailAddress-error") }
        mpNameError { $("#mpName-error") }
        mpPartyError { $("#mpParty-error") }
        mpConstituencyError { $("#mpConstituency-error") }
        mpEngagedError { $("#mpEngaged-error") }
        mpSympatheticError { $("#mpSympathetic-error") }
        schemesError { $("#schemes-error") }
        industryError { $("#industry-error") }
        howDidYouHearAboutLcagError { $("#howDidYouHearAboutLcag-error") }
        memberOfBigGroupError { $("#memberOfBigGroup-error") }
        bigGroupUsernameError { $("#bigGroupUsername-error") }
        identificationError { $("#identification-error") }
        proofOfSchemeInvolvementError { $("#proofOfSchemeInvolvement-error") }
        documentUploadErrorBanner { $("#documentUploadErrorBanner") }

        submitButton { $("#uploadButton") }
    }
}
