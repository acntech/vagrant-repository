package no.acntech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import no.acntech.service.SecurityService;
import no.acntech.service.UserService;

@Controller
public class BoxController {

    private final SecurityService securityService;
    private final UserService userService;

    public BoxController(final SecurityService securityService,
                         final UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @GetMapping(path = "/")
    public ModelAndView getIndexPage() {
        return new ModelAndView("index");
    }

    @GetMapping(path = "/about")
    public ModelAndView getAboutPage() {
        return new ModelAndView("about");
    }
}
