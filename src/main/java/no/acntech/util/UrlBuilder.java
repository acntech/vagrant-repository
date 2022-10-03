package no.acntech.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import no.acntech.model.ProviderType;

public final class UrlBuilder {

    private static final String PATH_PREFIX = "/api/v1";

    private UrlBuilder() {
    }

    public static URI userUri(final UriComponentsBuilder uriBuilder,
                              final String username) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/user/{username}")
                .buildAndExpand(username.toLowerCase())
                .toUri();
    }

    public static URI organizationUri(final UriComponentsBuilder uriBuilder,
                                      final String name) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/organization/{name}")
                .buildAndExpand(name.toLowerCase())
                .toUri();
    }

    public static URI organizationMemberUri(final UriComponentsBuilder uriBuilder,
                                            final String name,
                                            final String username) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/organization/{name}/user/{username}")
                .buildAndExpand(name.toLowerCase(), username.toLowerCase())
                .toUri();
    }

    public static URI boxUri(final UriComponentsBuilder uriBuilder,
                             final String username,
                             final String name) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/box/{username}/{name}")
                .buildAndExpand(username.toLowerCase(), name.toLowerCase())
                .toUri();
    }

    public static URI versionUri(final UriComponentsBuilder uriBuilder,
                                 final String username,
                                 final String name,
                                 final String version) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/box/{username}/{name}/version/{version}")
                .buildAndExpand(username.toLowerCase(), name.toLowerCase(), version)
                .toUri();
    }

    public static URI versionReleaseUri(final UriComponentsBuilder uriBuilder,
                                        final String username,
                                        final String name,
                                        final String version) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/box/{username}/{name}/version/{version}/release")
                .buildAndExpand(username.toLowerCase(), name.toLowerCase(), version)
                .toUri();
    }

    public static URI versionRevokeUri(final UriComponentsBuilder uriBuilder,
                                       final String username,
                                       final String name,
                                       final String version) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/box/{username}/{name}/version/{version}/revoke")
                .buildAndExpand(username.toLowerCase(), name.toLowerCase(), version)
                .toUri();
    }

    public static URI providerUri(final UriComponentsBuilder uriBuilder,
                                  final String username,
                                  final String name,
                                  final String version,
                                  final ProviderType providerType) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/box/{username}/{name}/version/{version}/provider/{provider}")
                .buildAndExpand(username.toLowerCase(), name.toLowerCase(), version, providerType.toString())
                .toUri();
    }

    public static URI uploadPathUri(final UriComponentsBuilder uriBuilder,
                                    final String uid) {
        return uriBuilder.cloneBuilder()
                .path(PATH_PREFIX)
                .path("/storage/{uid}")
                .buildAndExpand(uid)
                .toUri();
    }
}
