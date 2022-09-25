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
import no.acntech.model.PasswordForm;
import no.acntech.model.RegisterForm;
import no.acntech.model.UpdateUser;
import no.acntech.model.UserRole;
import no.acntech.service.SecurityService;
import no.acntech.service.UserService;

@Controller
public class UserController {

    private final SecurityService securityService;
    private final UserService userService;

    public UserController(final SecurityService securityService,
                          final UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
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
    public ModelAndView postRegisterPage(@ModelAttribute(name = "formData") @Valid @NotNull final RegisterForm form,
                                         final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = new ModelAndView("register");
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        userService.createUser(new CreateUser(form.getUsername(), form.getNewPassword(), UserRole.USER));
        return new ModelAndView("redirect:/login");
    }

    @GetMapping(path = "/password")
    public ModelAndView getPasswordPage() {
        final var modelAndView = new ModelAndView("password");
        modelAndView.addObject("formData", new PasswordForm());
        return modelAndView;
    }

    @PostMapping(path = "/password")
    public ModelAndView postPasswordPage(@ModelAttribute(name = "formData") @Valid @NotNull final PasswordForm form,
                                         final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = new ModelAndView("password");
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var username = securityService.getUsername();
        userService.updateUser(username, new UpdateUser(null, form.getCurrentPassword(), form.getNewPassword(), null));
        return new ModelAndView("redirect:/");
    }
}
