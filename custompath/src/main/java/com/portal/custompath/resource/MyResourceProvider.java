package com.portal.custompath.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;
import java.util.Map;
import jakarta.ws.rs.core.MediaType;

public class MyResourceProvider implements RealmResourceProvider {
    private final KeycloakSession session;

    public MyResourceProvider(KeycloakSession keycloakSession) {
        this.session = keycloakSession;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {

    }

    @GET
    @Path("/realm/name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response customer() {
        return Response.ok(Map.of("realm-name", session.getContext().getRealm().getName())).build();
    }

    @GET
    @Path("/user/login-name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response customerAuth() {
        AuthenticationManager.AuthResult auth = checkAuth();
        return Response.ok(Map.of("restricted-user", auth.getUser().getUsername())).build();
    }

    private AuthenticationManager.AuthResult checkAuth() {
        AuthenticationManager.AuthResult auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        if (auth == null) {
            throw new NotAuthorizedException("Bearer");
        }
        return auth;
    }
}
