package com.portal.passwordless.auth.token;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.actiontoken.AbstractActionTokenHandler;
import org.keycloak.authentication.actiontoken.ActionTokenContext;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventType;
import org.keycloak.models.ClientModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.utils.RedirectUtils;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.util.ResolveRelative;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicLinkActionTokenHandler extends AbstractActionTokenHandler<MagicLinkActionToken> {

    private static final Logger log = LoggerFactory.getLogger(MagicLinkActionTokenHandler.class);

    public MagicLinkActionTokenHandler() {
        super(
                MagicLinkActionToken.TOKEN_TYPE,
                MagicLinkActionToken.class,
                Messages.INVALID_REQUEST,
                EventType.EXECUTE_ACTION_TOKEN,
                Errors.INVALID_REQUEST);
    }

    @Override
    public AuthenticationSessionModel startFreshAuthenticationSession(
            MagicLinkActionToken token, ActionTokenContext<MagicLinkActionToken> tokenContext) {
        return tokenContext.createAuthenticationSessionForClient(token.getIssuedFor());
    }

    @Override
    public boolean canUseTokenRepeatedly(
            MagicLinkActionToken token, ActionTokenContext<MagicLinkActionToken> tokenContext) {
        return token
                .getActionTokenPersistent(); // Invalidate action token after one use if configured to do so
    }

    @Override
    public Response handleToken(MagicLinkActionToken token, ActionTokenContext<MagicLinkActionToken> tokenContext) {
        log.info("handleToken for iss: {}, user: {}", token.getIssuedFor(), token.getUserId());
        UserModel user = tokenContext.getAuthenticationSession().getAuthenticatedUser();

        final AuthenticationSessionModel authSession = tokenContext.getAuthenticationSession();
        final ClientModel client = authSession.getClient();
        final String redirectUri =
                token.getRedirectUri() != null
                        ? token.getRedirectUri()
                        : ResolveRelative.resolveRelativeUri(
                        tokenContext.getSession(), client.getRootUrl(), client.getBaseUrl());
        log.info("Using redirect_uri {}", redirectUri);

        String redirect =
                RedirectUtils.verifyRedirectUri(
                        tokenContext.getSession(), redirectUri, authSession.getClient());
        if (redirect != null) {
            authSession.setAuthNote(
                    AuthenticationManager.SET_REDIRECT_URI_AFTER_REQUIRED_ACTIONS, "true");
            authSession.setRedirectUri(redirect);
            authSession.setClientNote(OIDCLoginProtocol.REDIRECT_URI_PARAM, redirectUri);
            if (token.getState() != null) {
                authSession.setClientNote(OIDCLoginProtocol.STATE_PARAM, token.getState());
            }
            if (token.getNonce() != null) {
                authSession.setClientNote(OIDCLoginProtocol.NONCE_PARAM, token.getNonce());
                authSession.setUserSessionNote(OIDCLoginProtocol.NONCE_PARAM, token.getNonce());
            }
        }

        if (token.getScope() != null) {
            authSession.setClientNote(OAuth2Constants.SCOPE, token.getScope());
            AuthenticationManager.setClientScopesInSession(authSession);
        }

        if (token.getRememberMe() != null && token.getRememberMe()) {
            authSession.setAuthNote(Details.REMEMBER_ME, "true");
            tokenContext.getEvent().detail(Details.REMEMBER_ME, "true");
        } else {
            authSession.removeAuthNote(Details.REMEMBER_ME);
        }

        user.setEmailVerified(true);

        String nextAction =
                AuthenticationManager.nextRequiredAction(
                        tokenContext.getSession(),
                        authSession,
                        tokenContext.getRequest(),
                        tokenContext.getEvent());
        return AuthenticationManager.redirectToRequiredActions(
                tokenContext.getSession(),
                tokenContext.getRealm(),
                authSession,
                tokenContext.getUriInfo(),
                nextAction);
    }
}
