package com.okta.spring.example.controllers;

import com.okta.authn.sdk.AuthenticationStateHandlerAdapter;
import com.okta.authn.sdk.resource.AuthenticationResponse;
import com.okta.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowAuthenticationStateHandler extends AuthenticationStateHandlerAdapter {

    private Logger log = LoggerFactory.getLogger(WorkflowAuthenticationStateHandler.class);

    private boolean success = false;

    @Override
    public void handleUnknown(AuthenticationResponse unknownResponse) {
        log.error("User failed authentication");
    }

    @Override
    public void handleSuccess(AuthenticationResponse successResponse) {
        log.info(successResponse.getUser().getLogin()+" authenticated successfully");
        // a user is ONLY considered authenticated if a sessionToken exists
        if (Strings.hasLength(successResponse.getSessionToken())) {
            String relayState = successResponse.getRelayState();
            String dest = relayState != null ? relayState : "/";
            success = true;
        }
        // other state transition successful
    }

    public boolean isSuccess() {
        return success;
    }
}
