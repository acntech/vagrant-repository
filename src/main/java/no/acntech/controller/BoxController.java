package no.acntech.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.exception.ItemNotFoundException;
import no.acntech.model.BoxForm;
import no.acntech.model.CreateBox;
import no.acntech.model.CreateOrganization;
import no.acntech.model.OrganizationForm;
import no.acntech.service.BoxService;
import no.acntech.service.OrganizationService;
import no.acntech.service.SecurityService;

@Controller
public class BoxController {

    private final SecurityService securityService;
    private final OrganizationService organizationService;
    private final BoxService boxService;

    public BoxController(final SecurityService securityService,
                         final OrganizationService organizationService,
                         final BoxService boxService) {
        this.securityService = securityService;
        this.organizationService = organizationService;
        this.boxService = boxService;
    }

    @GetMapping(path = "/")
    public ModelAndView getIndexPage() {
        return new ModelAndView("index");
    }

    @GetMapping(path = "/about")
    public ModelAndView getAboutPage() {
        return new ModelAndView("about");
    }

    @GetMapping(path = "/organization/{name}")
    public ModelAndView getOrganizationByNamePage(@PathVariable("name") final String name) {
        final var modelAndView = new ModelAndView("organization");
        try {
            final var organization = organizationService.getOrganization(name);
            modelAndView.addObject("organization", organization);
        } catch (ItemNotFoundException e) {
            modelAndView.addObject("organization", null);
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
        }
        return modelAndView;
    }

    @GetMapping(path = "/organization")
    public ModelAndView getOrganizationPage() {
        final var modelAndView = new ModelAndView("organization");
        modelAndView.addObject("formData", new OrganizationForm());
        return modelAndView;
    }

    @PostMapping(path = "/organization")
    public ModelAndView postOrganizationPage(@ModelAttribute("formData") @Valid @NotNull final OrganizationForm form,
                                             final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = new ModelAndView("organization");
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        organizationService.createOrganization(new CreateOrganization(form.getName(), form.getDescription()));
        final var organization = organizationService.getOrganization(form.getName().toLowerCase());
        return new ModelAndView("redirect:/" + organization.name() + "/boxes");
    }

    @GetMapping(path = "/organizations")
    public ModelAndView getOrganizationsPage() {
        final var username = securityService.getUsername();
        final var organizations = organizationService.findOrganizations(username);
        final var modelAndView = new ModelAndView("organizations");
        modelAndView.addObject("organizations", organizations);
        return modelAndView;
    }

    @GetMapping(path = "/box")
    public ModelAndView getBoxPage() {
        final var username = securityService.getUsername();
        final var organizations = organizationService.findOrganizations(username);
        final var modelAndView = new ModelAndView("box");
        modelAndView.addObject("organizations", organizations);
        modelAndView.addObject("formData", new BoxForm());
        return modelAndView;
    }

    @PostMapping(path = "/box")
    public ModelAndView postBoxPage(@ModelAttribute("formData") @Valid @NotNull final BoxForm form,
                                    final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = new ModelAndView("box");
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        boxService.createBox(new CreateBox(form.getName(), form.getUsername(), form.getDescription(), form.getDescription(), form.getPrivate()));
        final var box = boxService.getBox(form.getUsername().toLowerCase(), form.getName().toLowerCase());
        return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name());
    }

    @GetMapping(path = "/{username}/boxes")
    public ModelAndView getBoxesPage(@PathVariable("username") final String username) {
        final var organization = organizationService.getOrganization(username);
        final var boxes = boxService.findBoxes(username);
        final var modelAndView = new ModelAndView("boxes");
        modelAndView.addObject("organization", organization);
        modelAndView.addObject("boxes", boxes);
        return modelAndView;
    }
}
