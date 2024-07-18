package com.portal.passwordless.resource;

import org.keycloak.models.KeycloakSession;

public class MagicLinkResourceProvider extends BaseRealmResourceProvider {

  public MagicLinkResourceProvider(KeycloakSession session) {
    super(session);
  }

  @Override
  public Object getRealmResource() {
    return new MagicLinkResource(session);
  }
}
