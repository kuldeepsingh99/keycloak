package com.portal.passwordless.auth.token;

import org.keycloak.Config;
import org.keycloak.authentication.actiontoken.ActionTokenHandler;
import org.keycloak.authentication.actiontoken.ActionTokenHandlerFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class MagicLinkActionTokenHandlerFactory implements ActionTokenHandlerFactory<MagicLinkActionToken> {

    public static final String PROVIDER_ID = "ext-magic-link";

    @Override
    public ActionTokenHandler<MagicLinkActionToken> create(KeycloakSession keycloakSession) {
        return new MagicLinkActionTokenHandler();
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
