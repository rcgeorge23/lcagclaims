package uk.co.novinet.web

import geb.Page

class ThankYouPage extends Page {

    static url = "http://localhost:8484/thankYou"

    static at = { title == "Loan Charge Action Group | Thank You" }

    static content = {

    }
}
