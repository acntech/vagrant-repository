package no.acntech.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.exception.ItemNotFoundException;
import no.acntech.model.CreateOrganization;
import no.acntech.model.CreateUser;
import no.acntech.model.CreateUserForm;
import no.acntech.model.OrganizationForm;
import no.acntech.model.PasswordForm;
import no.acntech.model.RegisterForm;
import no.acntech.model.UpdateOrganization;
import no.acntech.model.UpdateUser;
import no.acntech.model.UpdateUserForm;
import no.acntech.model.UserRole;
import no.acntech.service.OrganizationService;
import no.acntech.service.SecurityService;
import no.acntech.service.UserService;

@Controller
public class UserController {

    private final ConversionService conversionService;
    private final SecurityService securityService;
    private final UserService userService;
    private final OrganizationService organizationService;

    public UserController(final ConversionService conversionService,
                          final SecurityService securityService,
                          final UserService userService,
                          final OrganizationService organizationService) {
        this.conversionService = conversionService;
        this.securityService = securityService;
        this.userService = userService;
        this.organizationService = organizationService;
    }

    @GetMapping(path = "/login")
    public ModelAndView getLoginPage() {
        return new ModelAndView("login");
    }

    @GetMapping(path = "/register")
    public ModelAndView getRegisterPage() {
        final var modelAndView = new ModelAndView("register-user");
        modelAndView.addObject("formData", new RegisterForm());
        return modelAndView;
    }

    @PostMapping(path = "/register")
    public ModelAndView postRegisterPage(@ModelAttribute(name = "formData") @Valid @NotNull final RegisterForm form,
                                         final RedirectAttributes redirectAttributes,
                                         final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = new ModelAndView("register-user");
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        userService.createUser(new CreateUser(form.getUsername(), form.getNewPassword(), UserRole.USER));
        redirectAttributes.addAttribute("register", "success"); // TODO: Handle redirect params
        return new ModelAndView("redirect:/login");
    }

    @GetMapping(path = "/password")
    public ModelAndView getPasswordPage() {
        final var modelAndView = new ModelAndView("update-password");
        modelAndView.addObject("formData", new PasswordForm());
        return modelAndView;
    }

    @PostMapping(path = "/password")
    public ModelAndView postPasswordPage(@ModelAttribute(name = "formData") @Valid @NotNull final PasswordForm form,
                                         final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = new ModelAndView("update-password");
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var username = securityService.getUsername();
        userService.updateUser(username, new UpdateUser(null, form.getCurrentPassword(), form.getNewPassword(), null));
        return new ModelAndView("redirect:/");
    }

    @GetMapping(path = "/users")
    public ModelAndView getUsersPage() {
        final var users = userService.findUsers();
        final var modelAndView = new ModelAndView("users");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @GetMapping(path = "/user")
    public ModelAndView getCreateUserPage() {
        final var modelAndView = new ModelAndView("create-user");
        modelAndView.addObject("roles", UserRole.values());
        modelAndView.addObject("formData", new CreateUserForm());
        return modelAndView;
    }

    @PostMapping(path = "/user")
    public ModelAndView postCreateUserPage(@ModelAttribute(name = "formData") @Valid @NotNull final CreateUserForm form,
                                           final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getCreateUserPage();
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        userService.createUser(new CreateUser(form.getUsername(), form.getNewPassword(), form.getRole()));
        return new ModelAndView("redirect:/users");
    }

    @GetMapping(path = "/user/{username}")
    public ModelAndView getUpdateUserPage(@PathVariable(name = "username") final String username) {
        final var modelAndView = new ModelAndView("update-user");
        try {
            final var user = userService.getUser(username);
            modelAndView.addObject("user", user);
            modelAndView.addObject("roles", UserRole.values());
            modelAndView.addObject("formData", new UpdateUserForm(user.username(), user.role()));
        } catch (ItemNotFoundException e) {
            modelAndView.addObject("user", null);
            modelAndView.addObject("roles", UserRole.values());
            modelAndView.addObject("formData", new UpdateUserForm());
        }
        return modelAndView;
    }

    @PostMapping(path = "/user/{username}")
    public ModelAndView postUpdateUserPage(@PathVariable(name = "username") final String username,
                                           @ModelAttribute(name = "formData") @Valid @NotNull final UpdateUserForm form,
                                           final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getUpdateUserPage(username);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        userService.updateUser(username, new UpdateUser(form.getUsername(), form.getNewPassword(), null, form.getRole()));
        return new ModelAndView("redirect:/users");
    }

    @PostMapping(path = "/user/{username}/delete")
    public ModelAndView postDeleteUserPage(@PathVariable(name = "username") final String username) {
        final var user = userService.getUser(username);
        userService.deleteUser(user.username());
        return new ModelAndView("redirect:/users");
    }

    @GetMapping(path = "/organizations")
    public ModelAndView getOrganizationsPage() {
        final var username = securityService.getUsername();
        final var organizations = organizationService.findOrganizations(username);
        final var modelAndView = new ModelAndView("organizations");
        modelAndView.addObject("organizations", organizations);
        return modelAndView;
    }

    @GetMapping(path = "/organization")
    public ModelAndView getCreateOrganizationPage() {
        final var modelAndView = new ModelAndView("create-organization");
        modelAndView.addObject("formData", new OrganizationForm());
        return modelAndView;
    }

    @PostMapping(path = "/organization")
    public ModelAndView postCreateOrganizationPage(@ModelAttribute(name = "formData") @Valid @NotNull final OrganizationForm form,
                                                   final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getCreateOrganizationPage();
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var createOrganization = conversionService.convert(form, CreateOrganization.class);
        Assert.notNull(createOrganization, "Conversion of OrganizationForm to CreateOrganization produced null");
        final var username = securityService.getUsername();
        organizationService.createOrganization(username, createOrganization);
        final var organization = organizationService.getOrganization(form.getName().toLowerCase());
        return new ModelAndView("redirect:/" + organization.name() + "/boxes");
    }

    @GetMapping(path = "/organization/{name}")
    public ModelAndView getUpdateOrganizationPage(@PathVariable(name = "name") final String name) {
        final var modelAndView = new ModelAndView("update-organization");
        try {
            final var organization = organizationService.getOrganization(name);
            final var organizationForm = conversionService.convert(organization, OrganizationForm.class);
            modelAndView.addObject("organization", organization);
            modelAndView.addObject("formData", organizationForm);
        } catch (ItemNotFoundException e) {
            modelAndView.addObject("organization", null);
            modelAndView.addObject("formData", new OrganizationForm());
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
        }
        return modelAndView;
    }

    @PostMapping(path = "/organization/{name}")
    public ModelAndView postUpdateOrganizationPage(@PathVariable(name = "name") final String name,
                                                   @ModelAttribute(name = "formData") @Valid @NotNull final OrganizationForm form,
                                                   final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getUpdateOrganizationPage(name);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var updateOrganization = conversionService.convert(form, UpdateOrganization.class);
        Assert.notNull(updateOrganization, "Conversion of OrganizationForm to UpdateOrganization produced null");
        organizationService.updateOrganization(name, updateOrganization);
        final var organization = organizationService.getOrganization(form.getName().toLowerCase());
        return new ModelAndView("redirect:/" + organization.name() + "/boxes");
    }

    @PostMapping(path = "/organization/{name}/delete")
    public ModelAndView postDeleteOrganizationPage(@PathVariable(name = "name") final String name) {
        final var organization = organizationService.getOrganization(name);
        organizationService.deleteOrganization(organization.name());
        return new ModelAndView("redirect:/organizations");
    }
}
