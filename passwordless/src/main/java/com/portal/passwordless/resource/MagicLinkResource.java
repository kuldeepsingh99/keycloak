package com.portal.passwordless.resource;


import com.portal.passwordless.auth.token.MagicLinkActionToken;
import com.portal.passwordless.bean.MagicLinkRequest;
import com.portal.passwordless.bean.MagicLinkResponse;
import com.portal.passwordless.util.MagicLink;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.OptionalInt;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicLinkResource extends AbstractAdminResource {

  private static final Logger log = LoggerFactory.getLogger(MagicLinkResource.class);

  public MagicLinkResource(KeycloakSession session) {
    super(session);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public MagicLinkResponse createMagicLink(final MagicLinkRequest rep) {


    ClientModel client = session.clients().getClientByClientId(realm, rep.getClientId());
    if (client == null)
      throw new NotFoundException(String.format("Client with ID %s not found.", rep.getClientId()));

    if (!MagicLink.validateRedirectUri(session, rep.getRedirectUri(), client))
      throw new BadRequestException(
          String.format("redirectUri %s disallowed by client.", rep.getRedirectUri()));

    String emailOrUsername = rep.getEmail();
    boolean sendEmail = rep.isSendEmail();

    if (rep.getUsername() != null) {
      emailOrUsername = rep.getUsername();
      sendEmail = false;
    }

    UserModel user =
        MagicLink.getUser(
            session,
            realm,
            emailOrUsername
            );
    if (user == null)
      throw new NotFoundException(
          String.format(
              "User with email/username %s not found, and forceCreate is off.", emailOrUsername));

    MagicLinkActionToken token =
        MagicLink.createActionToken(
            user,
            rep.getClientId(),
            rep.getRedirectUri(),
            OptionalInt.of(rep.getExpirationSeconds()),
            rep.getScope(),
            rep.getNonce(),
            rep.getState(),
            rep.getRememberMe(),
            rep.getActionTokenPersistent());
    String link = MagicLink.linkFromActionToken(session, realm, token);
    boolean sent = false;
    if (sendEmail) {
      sent = MagicLink.sendMagicLinkEmail(session, user, link);
      log.info("sent email to {} {}. Link? {}", rep.getEmail(), sent, link);
    }

    MagicLinkResponse resp = new MagicLinkResponse();
    resp.setUserId(user.getId());
    resp.setLink(link);
    resp.setSent(sent);

    return resp;
  }
}
