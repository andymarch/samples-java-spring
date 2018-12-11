package com.okta.spring.example;

import com.okta.authn.sdk.AuthenticationException;
import com.okta.authn.sdk.client.AuthenticationClient;
import com.okta.authn.sdk.client.AuthenticationClients;
import com.okta.authn.sdk.resource.AuthenticationResponse;
import com.okta.spring.example.controllers.WorkflowAuthenticationStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OktaAuth {

    private static final Logger log = LoggerFactory.getLogger(OktaAuth.class);

    private AuthenticationClient client = AuthenticationClients.builder()
            .setOrgUrl("https://examply.okta-emea.com")
            .build();

    public AuthenticationResponse authenticate(String user, char[] pword, String redirect, WorkflowAuthenticationStateHandler handler){
        try {
            return client.authenticate(user,pword,redirect,new WorkflowAuthenticationStateHandler());
        } catch (AuthenticationException e) {
            log.error("Failed to authentication user "+user);
            return null;
        }
    }
}
