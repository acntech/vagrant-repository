package no.acntech.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.model.CreateUser;
import no.acntech.model.RegisterForm;
import no.acntech.model.UserRole;
import no.acntech.service.UserService;

@Controller
public class ViewController {

    private final UserService userService;

    public ViewController(final UserService userService) {
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

    @GetMapping(path = "/login")
    public ModelAndView getLoginPage() {
        return new ModelAndView("login");
    }

    @GetMapping(path = "/register")
    public ModelAndView getRegisterPage() {
        final var modelAndView = new ModelAndView("register");
        modelAndView.addObject("formData", new RegisterForm());
        return modelAndView;
    }

    @PostMapping(path = "/register")
    public ModelAndView postRegisterPage(@ModelAttribute("formData") @Valid @NotNull final RegisterForm registerForm,
                                         final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = new ModelAndView("register");
            modelAndView.addObject("formData", registerForm);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        userService.createUser(new CreateUser(registerForm.getUsername(), registerForm.getNewPassword(), UserRole.USER));
        return new ModelAndView("redirect:/login");
    }
}
