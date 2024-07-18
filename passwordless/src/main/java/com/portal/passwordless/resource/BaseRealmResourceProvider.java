package com.portal.passwordless.resource;

import org.keycloak.http.HttpRequest;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

/** */

public abstract class BaseRealmResourceProvider implements RealmResourceProvider {

  protected final KeycloakSession session;

  public BaseRealmResourceProvider(KeycloakSession session) {
    this.session = session;
  }

  @Override
  public void close() {}

  protected abstract Object getRealmResource();

  @Override
  public Object getResource() {
      return getRealmResource();
  }
}
