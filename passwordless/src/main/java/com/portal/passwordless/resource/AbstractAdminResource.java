package com.portal.passwordless.resource;

import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.AdminEventBuilder;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAdminResource {

  private static final Logger log = LoggerFactory.getLogger(AbstractAdminResource.class);

  protected final ClientConnection connection;
  protected final HttpHeaders headers;
  protected final KeycloakSession session;
  protected final RealmModel realm;

  protected AbstractAdminResource(KeycloakSession session) {
    this.session = session;
    this.realm = session.getContext().getRealm();
    this.headers = session.getContext().getRequestHeaders();
    this.connection = session.getContext().getConnection();
  }
}
