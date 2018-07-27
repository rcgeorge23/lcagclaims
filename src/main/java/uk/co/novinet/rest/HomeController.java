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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
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
        Member member = memberService.findMemberByToken(token);

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
            @RequestParam("identification") MultipartFile identification,
            @RequestParam("proofOfSchemeInvolvement") MultipartFile proofOfSchemeInvolvement,
            Member member,
            ModelMap model) {
        try {
            memberService.update(member);

            if (member.hasCompletedMembershipForm()) {
                mailSenderService.sendFollowUpEmail(member);
                return new ModelAndView("thankYou", model);
            }

            return new ModelAndView("redirect:/", model);
        } catch (Exception e) {
            LOGGER.error("Unable to receive files and update member {}", member, e);
            return new ModelAndView("error", model);
        }
    }

    private String sanitise(String toSanitise) {
        return toSanitise.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

}