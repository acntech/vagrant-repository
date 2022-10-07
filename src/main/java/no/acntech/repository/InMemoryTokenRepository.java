package no.acntech.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import no.acntech.model.Token;

@Repository
public class InMemoryTokenRepository implements TokenRepository {

    private final Map<String, Token> tokenStore = new ConcurrentHashMap<>();

    @Override
    public Token loadToken(String key) {
        return tokenStore.get(key);
    }

    @Override
    public void saveToken(String key, Token token) {
        tokenStore.put(key, token);
    }

    @Override
    public void removeToken(String key) {
        tokenStore.remove(key);
    }

    @Override
    public boolean containsToken(String key) {
        return tokenStore.containsKey(key);
    }
}
