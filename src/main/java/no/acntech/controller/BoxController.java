package no.acntech.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.exception.ChecksumException;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.model.Algorithm;
import no.acntech.model.BoxForm;
import no.acntech.model.CreateBox;
import no.acntech.model.CreateOrganization;
import no.acntech.model.CreateProvider;
import no.acntech.model.CreateVersion;
import no.acntech.model.OrganizationForm;
import no.acntech.model.ProviderForm;
import no.acntech.model.ProviderType;
import no.acntech.model.VersionForm;
import no.acntech.service.BoxService;
import no.acntech.service.OrganizationService;
import no.acntech.service.ProviderService;
import no.acntech.service.SecurityService;
import no.acntech.service.StorageService;
import no.acntech.service.UploadService;
import no.acntech.service.VersionService;

@Controller
public class BoxController {

    private final SecurityService securityService;
    private final OrganizationService organizationService;
    private final BoxService boxService;
    private final VersionService versionService;
    private final ProviderService providerService;
    private final UploadService uploadService;
    private final StorageService storageService;

    public BoxController(final SecurityService securityService,
                         final OrganizationService organizationService,
                         final BoxService boxService,
                         final VersionService versionService,
                         final ProviderService providerService,
                         final UploadService uploadService,
                         final StorageService storageService) {
        this.securityService = securityService;
        this.organizationService = organizationService;
        this.boxService = boxService;
        this.versionService = versionService;
        this.providerService = providerService;
        this.uploadService = uploadService;
        this.storageService = storageService;
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
    public ModelAndView getOrganizationByNamePage(@PathVariable(name = "name") final String name) {
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
    public ModelAndView postOrganizationPage(@ModelAttribute(name = "formData") @Valid @NotNull final OrganizationForm form,
                                             final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getOrganizationPage();
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var username = securityService.getUsername();
        organizationService.createOrganization(username, new CreateOrganization(form.getName(), form.getDescription()));
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
    public ModelAndView postBoxPage(@ModelAttribute(name = "formData") @Valid @NotNull final BoxForm form,
                                    final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getBoxPage();
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var organization = organizationService.getOrganization(form.getUsername());
        boxService.createBox(new CreateBox(form.getName(), organization.name(), form.getDescription(), form.getDescription(), form.getPrivate()));
        final var box = boxService.getBox(organization.name(), form.getName().toLowerCase());
        return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name());
    }

    @GetMapping(path = "/{username}/boxes")
    public ModelAndView getBoxesPage(@PathVariable(name = "username") final String username) {
        final var organization = organizationService.getOrganization(username);
        final var boxes = boxService.findBoxes(organization.name());
        final var modelAndView = new ModelAndView("boxes");
        modelAndView.addObject("organization", organization);
        modelAndView.addObject("boxes", boxes);
        return modelAndView;
    }

    @GetMapping(path = "/{username}/boxes/{name}")
    public ModelAndView getVersionsPage(@PathVariable(name = "username") final String username,
                                        @PathVariable(name = "name") final String name) {
        final var box = boxService.getBox(username, name);
        final var versions = versionService.findVersions(box.username(), box.name());
        final var modelAndView = new ModelAndView("versions");
        modelAndView.addObject("box", box);
        modelAndView.addObject("versions", versions);
        return modelAndView;
    }

    @GetMapping(path = "/{username}/boxes/{name}/version")
    public ModelAndView getVersionPage(@PathVariable(name = "username") final String username,
                                       @PathVariable(name = "name") final String name) {
        final var box = boxService.getBox(username, name);
        final var modelAndView = new ModelAndView("version");
        modelAndView.addObject("box", box);
        modelAndView.addObject("formData", new VersionForm());
        return modelAndView;
    }

    @PostMapping(path = "/{username}/boxes/{name}/version")
    public ModelAndView postVersionPage(@PathVariable(name = "username") final String username,
                                        @PathVariable(name = "name") final String name,
                                        @ModelAttribute(name = "formData") @Valid @NotNull final VersionForm form,
                                        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getVersionPage(username, name);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var box = boxService.getBox(username, name);
        versionService.createVersion(box.username(), box.name(), new CreateVersion(form.getName(), form.getDescription()));
        final var version = versionService.getVersion(username, name, form.getName());
        return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name() + "/versions/" + version.name());
    }

    @GetMapping(path = "/{username}/boxes/{name}/versions/{version}")
    public ModelAndView getProvidersPage(@PathVariable(name = "username") final String username,
                                         @PathVariable(name = "name") final String name,
                                         @PathVariable(name = "version") final String versionParam) {
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var providers = providerService.findProviders(box.username(), box.name(), version.name());
        final var modelAndView = new ModelAndView("providers");
        modelAndView.addObject("box", box);
        modelAndView.addObject("version", version);
        modelAndView.addObject("providers", providers);
        return modelAndView;
    }

    @GetMapping(path = "/{username}/boxes/{name}/versions/{version}/provider")
    public ModelAndView getProviderPage(@PathVariable(name = "username") final String username,
                                        @PathVariable(name = "name") final String name,
                                        @PathVariable(name = "version") final String versionParam) {
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var modelAndView = new ModelAndView("provider");
        modelAndView.addObject("box", box);
        modelAndView.addObject("version", version);
        modelAndView.addObject("providerTypes", ProviderType.values());
        modelAndView.addObject("checksumTypes", Algorithm.values());
        modelAndView.addObject("formData", new ProviderForm());
        return modelAndView;
    }

    @PostMapping(path = "/{username}/boxes/{name}/versions/{version}/provider")
    public ModelAndView postProviderPage(@PathVariable(name = "username") final String username,
                                         @PathVariable(name = "name") final String name,
                                         @PathVariable(name = "version") final String versionParam,
                                         @ModelAttribute(name = "formData") @Valid @NotNull final ProviderForm form,
                                         final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getProviderPage(username, name, versionParam);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var createProvider = new CreateProvider(form.getName(), form.getChecksum(), form.getChecksumType(), form.getHosted(), form.getUrl());
        providerService.createProvider(box.username(), box.name(), version.name(), createProvider);
        return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name() + "/versions/" + version.name());
    }

    @GetMapping(path = "/{username}/boxes/{name}/versions/{version}/providers/{provider}")
    public ModelAndView getUploadPage(@PathVariable(name = "username") final String username,
                                      @PathVariable(name = "name") final String name,
                                      @PathVariable(name = "version") final String versionParam,
                                      @PathVariable(name = "provider") final String providerParam,
                                      final UriComponentsBuilder uriBuilder) {
        final var providerType = ProviderType.fromProvider(providerParam);
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var provider = providerService.getProvider(box.username(), box.name(), version.name(), providerType);
        final var modelAndView = new ModelAndView("upload");
        modelAndView.addObject("box", box);
        modelAndView.addObject("version", version);
        modelAndView.addObject("provider", provider);
        try {
            final var upload = uploadService.getUpload(box.username(), box.name(), version.name(), providerType);
            final var uploadPathUrl = uriBuilder.path("/api/storage/{uid}")
                    .buildAndExpand(upload.uid())
                    .toUri().toString();
            modelAndView.addObject("upload", upload.with(uploadPathUrl));
        } catch (ItemNotFoundException e) {
            modelAndView.addObject("upload", null);
        }
        return modelAndView;
    }

    @PostMapping(path = "/{username}/boxes/{name}/versions/{version}/providers/{provider}")
    public ModelAndView postUploadPage(@PathVariable(name = "username") final String username,
                                       @PathVariable(name = "name") final String name,
                                       @PathVariable(name = "version") final String versionParam,
                                       @PathVariable(name = "provider") final String providerParam,
                                       final UriComponentsBuilder uriBuilder) {
        final var modelAndView = getUploadPage(username, name, versionParam, providerParam, uriBuilder);
        final var uid = uploadService.createUpload(username, name, versionParam, ProviderType.fromProvider(providerParam));
        final var upload = uploadService.getUpload(uid);
        final var uploadPathUrl = uriBuilder.path("/api/storage/{uid}")
                .buildAndExpand(uid)
                .toUri().toString();
        modelAndView.addObject("upload", upload.with(uploadPathUrl));
        return modelAndView;
    }

    @PostMapping(path = "/{username}/boxes/{name}/versions/{version}/providers/{provider}/upload/{uid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView postStorePage(@PathVariable(name = "username") final String username,
                                      @PathVariable(name = "name") final String name,
                                      @PathVariable(name = "version") final String versionParam,
                                      @PathVariable(name = "provider") final String providerParam,
                                      @PathVariable(name = "uid") final String uid,
                                      @RequestParam(name = "file") final MultipartFile file,
                                      final UriComponentsBuilder uriBuilder) {
        final var modelAndView = getUploadPage(username, name, versionParam, providerParam, uriBuilder);
        try {
            final var storage = storageService.saveFile(uid, file);
            uploadService.updateUpload(storage.uid(), storage.fileSize(), storage.checksum());
            uploadService.verifyUpload(uid);
            modelAndView.addObject("verified", true);
            return modelAndView;
        } catch (ChecksumException e) {
            modelAndView.addObject("verified", false);
            modelAndView.setStatus(HttpStatus.NOT_ACCEPTABLE);
            return modelAndView;
        }
    }
}
