package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Service
public class HttpRequestService extends CookieGenerator {

    public static final String BEARER_TOKEN_PREFIX = "Bearer";

    public HttpRequestService(@Value("${acntech.security.cookie.name}") final String cookieName,
                              @Value("${acntech.security.cookie.max-age}") final Integer cookieMaxAge,
                              @Value("${acntech.security.cookie.http-only}") final Boolean httpOnly) {
        this.setCookieName(cookieName);
        this.setCookieMaxAge(cookieMaxAge);
        this.setCookieHttpOnly(httpOnly);
    }

    public String readCookie(final HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest cannot be null");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        } else {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(this.getCookieName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
    }

    public String readHeader(final HttpServletRequest request) {
        final var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return getBearerToken(header);
    }

    public String getBearerToken(final String header) {
        if (StringUtils.isBlank(header)) {
            return null;
        }
        if (!StringUtils.startsWithIgnoreCase(header.trim(), BEARER_TOKEN_PREFIX)) {
            return null;
        }
        return header.replace(BEARER_TOKEN_PREFIX, "").trim();
    }
}
