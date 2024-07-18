package com.portal.passwordless.util;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.portal.passwordless.auth.token.MagicLinkActionToken;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.keycloak.authentication.actiontoken.DefaultActionToken;
import org.keycloak.common.util.Time;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.utils.RedirectUtils;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import org.keycloak.services.Urls;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.resources.RealmsResource;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicLink {

    private static final Logger log = LoggerFactory.getLogger(MagicLink.class);

    public static UserModel getUser(
            KeycloakSession session,
            RealmModel realm,
            String email) {
        return KeycloakModelUtils.findUserByNameOrEmail(session, realm, email);
    }


    public static MagicLinkActionToken createActionToken(
            UserModel user,
            String clientId,
            OptionalInt validity,
            Boolean rememberMe,
            AuthenticationSessionModel authSession) {
        return createActionToken(user, clientId, validity, rememberMe, authSession, true);
    }



    public static MagicLinkActionToken createActionToken(
            UserModel user,
            String clientId,
            OptionalInt validity,
            Boolean rememberMe,
            AuthenticationSessionModel authSession,
            Boolean isActionTokenPersistent) {
        String redirectUri = authSession.getRedirectUri();
        String scope = authSession.getClientNote(OIDCLoginProtocol.SCOPE_PARAM);
        String state = authSession.getClientNote(OIDCLoginProtocol.STATE_PARAM);
        String nonce = authSession.getClientNote(OIDCLoginProtocol.NONCE_PARAM);
        log.info(
                "Attempting MagicLinkAuthenticator for {}, {}, {}", user.getEmail(), clientId, redirectUri);

        log.info("MagicLinkAuthenticator extra vars {}, {}, {} {}", scope, state, nonce, rememberMe);

        return createActionToken(
                user,
                clientId,
                redirectUri,
                validity,
                scope,
                nonce,
                state,
                rememberMe,
                isActionTokenPersistent);
    }

    public static MagicLinkActionToken createActionToken(
            UserModel user,
            String clientId,
            String redirectUri,
            OptionalInt validity,
            String scope,
            String nonce,
            String state,
            Boolean rememberMe) {
        return createActionToken(
                user, clientId, redirectUri, validity, scope, nonce, state, rememberMe, true);
    }

    public static MagicLinkActionToken createActionToken(
            UserModel user,
            String clientId,
            String redirectUri,
            OptionalInt validity,
            String scope,
            String nonce,
            String state,
            Boolean rememberMe,
            Boolean isActionTokenPersistent) {

        // build the action token
        int validityInSecs = validity.orElse(60 * 60 * 24); // 1 day
        int absoluteExpirationInSecs = Time.currentTime() + validityInSecs;

        MagicLinkActionToken token =
                new MagicLinkActionToken(
                        user.getId(),
                        absoluteExpirationInSecs,
                        clientId,
                        redirectUri,
                        scope,
                        nonce,
                        state,
                        rememberMe,
                        isActionTokenPersistent);
        return token;
    }

    public static MagicLinkActionToken createActionToken(
            UserModel user, String clientId, String redirectUri, OptionalInt validity) {
        return createActionToken(user, clientId, redirectUri, validity, null, null, null, false, true);
    }

    public static String linkFromActionToken(
            KeycloakSession session, RealmModel realm, DefaultActionToken token) {
        UriInfo uriInfo = session.getContext().getUri();


        RealmModel r = session.getContext().getRealm();

        session.getContext().setRealm(realm);

        UriBuilder builder =
                actionTokenBuilder(
                        uriInfo.getBaseUri(), token.serialize(session, realm, uriInfo), token.getIssuedFor());

        // and then set it back
        session.getContext().setRealm(r);
        return builder.build(realm.getName()).toString();
    }

    public static boolean validateRedirectUri(
            KeycloakSession session, String redirectUri, ClientModel client) {
        String redirect = RedirectUtils.verifyRedirectUri(session, redirectUri, client);
        return (redirectUri.equals(redirect));
    }

    private static UriBuilder actionTokenBuilder(URI baseUri, String tokenString, String clientId) {
        return Urls.realmBase(baseUri)
                .path(RealmsResource.class, "getLoginActionsService")
                .path(LoginActionsService.class, "executeActionToken")
                .queryParam(Constants.KEY, tokenString)
                .queryParam(Constants.CLIENT_ID, clientId);
    }

    public static boolean sendMagicLinkEmail(KeycloakSession session, UserModel user, String link) {
        RealmModel realm = session.getContext().getRealm();
        try {
            EmailTemplateProvider emailTemplateProvider =
                    session.getProvider(EmailTemplateProvider.class);
            String realmName = getRealmName(realm);
            List<Object> subjAttr = ImmutableList.of(realmName);
            Map<String, Object> bodyAttr = Maps.newHashMap();
            bodyAttr.put("realmName", realmName);
            bodyAttr.put("magicLink", link);
            emailTemplateProvider
                    .setRealm(realm)
                    .setUser(user)
                    .setAttribute("realmName", realmName)
                    .send("magicLinkSubject", subjAttr, "magic-link-email.ftl", bodyAttr);
            return true;
        } catch (EmailException e) {
            log.error("Failed to send magic link email", e);
        }
        return false;
    }



    public static String getRealmName(RealmModel realm) {
        return Strings.isNullOrEmpty(realm.getDisplayName()) ? realm.getName() : realm.getDisplayName();
    }


}
