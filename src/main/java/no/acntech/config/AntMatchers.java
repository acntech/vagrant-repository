package no.acntech.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;

public final class AntMatchers {

    private AntMatchers() {
    }

    public static RequestMatcher antMatcher(final String path) {
        return new AntPathRequestMatcher(path);
    }

    public static RequestMatcher antMatcher(final String path, final HttpMethod httpMethod) {
        return new AntPathRequestMatcher(path, httpMethod.name());
    }

    public static RequestMatcher negatedAntMatcher(final String path) {
        return new NegatedRequestMatcher(antMatcher(path));
    }

    public static RequestMatcher andMatcher(final RequestMatcher... requestMatchers) {
        return new AndRequestMatcher(requestMatchers);
    }

    public static RequestMatcher anyMatcher() {
        return AnyRequestMatcher.INSTANCE;
    }

    public static RequestMatcher anyExceptMatcher(final String... paths) {
        if (paths == null || paths.length == 0) {
            return anyMatcher();
        } else {
            final var requestMatchers = new ArrayList<RequestMatcher>();
            requestMatchers.add(anyMatcher());
            for (final String path : paths) {
                requestMatchers.add(negatedAntMatcher(path));
            }
            return andMatcher(requestMatchers.toArray(new RequestMatcher[0]));
        }
    }
}
