package no.acntech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    @GetMapping("/")
    public ModelAndView indexPage() {
        return new ModelAndView("index");
    }

    @GetMapping("/about")
    public ModelAndView aboutPage() {
        return new ModelAndView("about");
    }

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }
}
