package uk.co.novinet.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.co.novinet.service.Claim;
import uk.co.novinet.service.MailSenderService;
import uk.co.novinet.service.Member;
import uk.co.novinet.service.MemberService;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private MailSenderService mailSenderService;

    @GetMapping("/")
    public String get(@RequestParam(value = "token", required = false) String token) {
        Member member = memberService.findMemberByClaimToken(token);

        if (member == null) {
            return "notFound";
        }

        if (member.hasCompletedMembershipForm()) {
            return "thankYou";
        }

        return "home";
    }

    @CrossOrigin
    @PostMapping(path = "/submit")
    public ModelAndView submit(
            Claim claim,
            ModelMap model) {
        Member existingMember = null;

        try {
            memberService.update(claim);

            existingMember = memberService.findMemberByClaimToken(claim.getClaimToken());

            mailSenderService.sendFollowUpEmail(existingMember);

            memberService.markHasBeenSentClaimConfirmationEmail(existingMember);

            return new ModelAndView("thankYou", model);
        } catch (Exception e) {
            LOGGER.error("Unable to receive files and update member {}", existingMember, e);
            return new ModelAndView("error", model);
        }
    }

}