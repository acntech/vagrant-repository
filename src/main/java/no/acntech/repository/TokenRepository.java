package no.acntech.repository;

import no.acntech.model.Token;

public interface TokenRepository {

    Token loadToken(String key);

    void saveToken(String key, Token token);

    void removeToken(String key);

    boolean containsToken(String key);
}
