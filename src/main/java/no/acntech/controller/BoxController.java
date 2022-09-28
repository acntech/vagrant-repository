package no.acntech.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
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
import no.acntech.model.CreateBox;
import no.acntech.model.CreateBoxForm;
import no.acntech.model.CreateOrganization;
import no.acntech.model.CreateProvider;
import no.acntech.model.CreateVersion;
import no.acntech.model.OrganizationForm;
import no.acntech.model.ProviderForm;
import no.acntech.model.ProviderType;
import no.acntech.model.UpdateBox;
import no.acntech.model.UpdateBoxForm;
import no.acntech.model.UpdateOrganization;
import no.acntech.model.UpdateProvider;
import no.acntech.model.UpdateVersion;
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

    private final ConversionService conversionService;
    private final SecurityService securityService;
    private final OrganizationService organizationService;
    private final BoxService boxService;
    private final VersionService versionService;
    private final ProviderService providerService;
    private final UploadService uploadService;
    private final StorageService storageService;

    public BoxController(final ConversionService conversionService,
                         final SecurityService securityService,
                         final OrganizationService organizationService,
                         final BoxService boxService,
                         final VersionService versionService,
                         final ProviderService providerService,
                         final UploadService uploadService,
                         final StorageService storageService) {
        this.conversionService = conversionService;
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
        final var username = securityService.getUsername();
        organizationService.createOrganization(username, new CreateOrganization(form.getName(), form.getDescription()));
        final var organization = organizationService.getOrganization(form.getName().toLowerCase());
        return new ModelAndView("redirect:/" + organization.name() + "/boxes");
    }

    @GetMapping(path = "/organization/{name}")
    public ModelAndView getUpdateOrganizationPage(@PathVariable(name = "name") final String name) {
        final var modelAndView = new ModelAndView("update-organization");
        try {
            final var organization = organizationService.getOrganization(name);
            modelAndView.addObject("organization", organization);
            modelAndView.addObject("formData", new OrganizationForm(organization.name(), organization.description()));
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
        organizationService.updateOrganization(name, new UpdateOrganization(form.getName(), form.getDescription()));
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
    public ModelAndView getCreateBoxPage() {
        final var username = securityService.getUsername();
        final var organizations = organizationService.findOrganizations(username);
        final var modelAndView = new ModelAndView("create-box");
        modelAndView.addObject("organizations", organizations);
        modelAndView.addObject("formData", new CreateBoxForm());
        return modelAndView;
    }

    @PostMapping(path = "/box")
    public ModelAndView postCreateBoxPage(@ModelAttribute(name = "formData") @Valid @NotNull final CreateBoxForm form,
                                          final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getCreateBoxPage();
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var organization = organizationService.getOrganization(form.getUsername());
        boxService.createBox(new CreateBox(form.getName(), organization.name(), form.getDescription(), form.getDescription(), form.getPrivate()));
        final var box = boxService.getBox(organization.name(), form.getName().toLowerCase());
        return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name());
    }

    @GetMapping(path = "/box/{username}/{name}")
    public ModelAndView getUpdateBoxPage(@PathVariable(name = "username") final String username,
                                         @PathVariable(name = "name") final String name) {
        final var modelAndView = new ModelAndView("update-box");
        try {
            final var organization = organizationService.getOrganization(username);
            final var box = boxService.getBox(username, name);
            modelAndView.addObject("organization", organization);
            modelAndView.addObject("box", box);
            modelAndView.addObject("formData", new UpdateBoxForm(box.name(), box.descriptionShort(), box.isPrivate()));
        } catch (ItemNotFoundException e) {
            modelAndView.addObject("organization", null);
            modelAndView.addObject("box", null);
            modelAndView.addObject("formData", new UpdateBoxForm());
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
        }
        return modelAndView;
    }

    @PostMapping(path = "/box/{username}/{name}")
    public ModelAndView postUpdateBoxPage(@PathVariable(name = "username") final String username,
                                          @PathVariable(name = "name") final String name,
                                          @ModelAttribute(name = "formData") @Valid @NotNull final UpdateBoxForm form,
                                          final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getUpdateBoxPage(username, name);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        boxService.updateBox(username, name, new UpdateBox(form.getName(), form.getDescription(), form.getDescription(), form.getPrivate()));
        final var box = boxService.getBox(username, form.getName().toLowerCase());
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
    public ModelAndView getCreateVersionPage(@PathVariable(name = "username") final String username,
                                             @PathVariable(name = "name") final String name) {
        final var box = boxService.getBox(username, name);
        final var modelAndView = new ModelAndView("create-version");
        modelAndView.addObject("box", box);
        modelAndView.addObject("formData", new VersionForm());
        return modelAndView;
    }

    @PostMapping(path = "/{username}/boxes/{name}/version")
    public ModelAndView postCreateVersionPage(@PathVariable(name = "username") final String username,
                                              @PathVariable(name = "name") final String name,
                                              @ModelAttribute(name = "formData") @Valid @NotNull final VersionForm form,
                                              final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getCreateVersionPage(username, name);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var box = boxService.getBox(username, name);
        versionService.createVersion(box.username(), box.name(), new CreateVersion(form.getName(), form.getDescription()));
        final var version = versionService.getVersion(box.username(), box.name(), form.getName());
        return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name() + "/versions/" + version.name());
    }

    @GetMapping(path = "/{username}/boxes/{name}/version/{version}")
    public ModelAndView getUpdateVersionPage(@PathVariable(name = "username") final String username,
                                             @PathVariable(name = "name") final String name,
                                             @PathVariable(name = "version") final String versionParam) {
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(username, name, versionParam);
        final var modelAndView = new ModelAndView("update-version");
        modelAndView.addObject("box", box);
        modelAndView.addObject("version", version);
        modelAndView.addObject("formData", new VersionForm(version.name(), version.descriptionHtml()));
        return modelAndView;
    }

    @PostMapping(path = "/{username}/boxes/{name}/version/{version}")
    public ModelAndView postUpdateVersionPage(@PathVariable(name = "username") final String username,
                                              @PathVariable(name = "name") final String name,
                                              @PathVariable(name = "version") final String versionParam,
                                              @ModelAttribute(name = "formData") @Valid @NotNull final VersionForm form,
                                              final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getUpdateVersionPage(username, name, versionParam);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var box = boxService.getBox(username, name);
        versionService.updateVersion(box.username(), box.name(), versionParam, new UpdateVersion(form.getName(), form.getDescription()));
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
    public ModelAndView getCreateProviderPage(@PathVariable(name = "username") final String username,
                                              @PathVariable(name = "name") final String name,
                                              @PathVariable(name = "version") final String versionParam) {
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var modelAndView = new ModelAndView("create-provider");
        modelAndView.addObject("box", box);
        modelAndView.addObject("version", version);
        modelAndView.addObject("providerTypes", ProviderType.values());
        modelAndView.addObject("checksumTypes", Algorithm.values());
        modelAndView.addObject("formData", new ProviderForm());
        return modelAndView;
    }

    @PostMapping(path = "/{username}/boxes/{name}/versions/{version}/provider")
    public ModelAndView postCreateProviderPage(@PathVariable(name = "username") final String username,
                                               @PathVariable(name = "name") final String name,
                                               @PathVariable(name = "version") final String versionParam,
                                               @ModelAttribute(name = "formData") @Valid @NotNull final ProviderForm form,
                                               final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final var modelAndView = getCreateProviderPage(username, name, versionParam);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var createProvider = conversionService.convert(form, CreateProvider.class);
        Assert.notNull(createProvider, "Converting ProviderForm to CreateProvider produced null");
        providerService.createProvider(box.username(), box.name(), version.name(), createProvider);
        final var provider = providerService.getProvider(box.username(), box.name(), version.name(), createProvider.name());
        if (provider.hosted()) {
            return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name() + "/versions/" + version.name() + "/providers/" + provider.name() + "/upload");
        } else {
            return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name() + "/versions/" + version.name() + "/providers");
        }
    }

    @GetMapping(path = "/{username}/boxes/{name}/versions/{version}/provider/{provider}")
    public ModelAndView getUpdateProviderPage(@PathVariable(name = "username") final String username,
                                              @PathVariable(name = "name") final String name,
                                              @PathVariable(name = "version") final String versionParam,
                                              @PathVariable(name = "provider") final String providerParam) {
        final var providerType = ProviderType.fromProvider(providerParam);
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var provider = providerService.getProvider(box.username(), box.name(), version.name(), providerType);
        final var modelAndView = new ModelAndView("update-provider");
        modelAndView.addObject("box", box);
        modelAndView.addObject("version", version);
        modelAndView.addObject("provider", provider);
        modelAndView.addObject("providerTypes", ProviderType.values());
        modelAndView.addObject("checksumTypes", Algorithm.values());
        modelAndView.addObject("formData", new ProviderForm(provider.name(), provider.checksumType(), provider.checksum(), provider.downloadUrl()));
        return modelAndView;
    }

    @SuppressWarnings("DuplicatedCode")
    @PostMapping(path = "/{username}/boxes/{name}/versions/{version}/provider/{provider}")
    public ModelAndView postUpdateProviderPage(@PathVariable(name = "username") final String username,
                                               @PathVariable(name = "name") final String name,
                                               @PathVariable(name = "version") final String versionParam,
                                               @PathVariable(name = "provider") final String providerParam,
                                               @ModelAttribute(name = "formData") @Valid @NotNull final ProviderForm form,
                                               final BindingResult bindingResult) {
        final var providerType = ProviderType.fromProvider(providerParam);
        if (bindingResult.hasErrors()) {
            final var modelAndView = getUpdateProviderPage(username, name, versionParam, providerParam);
            modelAndView.addObject("formData", form);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        final var box = boxService.getBox(username, name);
        final var version = versionService.getVersion(box.username(), box.name(), versionParam);
        final var updateProvider = conversionService.convert(form, UpdateProvider.class);
        Assert.notNull(updateProvider, "Converting ProviderForm to UpdateProvider produced null");
        providerService.updateProvider(box.username(), box.name(), version.name(), providerType, updateProvider);
        final var provider = providerService.getProvider(box.username(), box.name(), version.name(), updateProvider.name());
        if (provider.hosted()) {
            return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name() + "/versions/" + version.name() + "/providers/" + provider.name() + "/upload");
        } else {
            return new ModelAndView("redirect:/" + box.username() + "/boxes/" + box.name() + "/versions/" + version.name() + "/providers");
        }
    }

    @GetMapping(path = "/{username}/boxes/{name}/versions/{version}/providers/{provider}/upload")
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
            final var uid = uploadService.createUpload(username, name, versionParam, ProviderType.fromProvider(providerParam));
            final var upload = uploadService.getUpload(uid);
            final var uploadPathUrl = uriBuilder.path("/api/storage/{uid}")
                    .buildAndExpand(upload.uid())
                    .toUri().toString();
            modelAndView.addObject("upload", upload.with(uploadPathUrl));
        }
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
