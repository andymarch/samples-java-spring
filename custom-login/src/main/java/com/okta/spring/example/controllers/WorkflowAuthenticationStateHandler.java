package com.okta.spring.example.controllers;

import com.okta.authn.sdk.AuthenticationStateHandlerAdapter;
import com.okta.authn.sdk.resource.AuthenticationResponse;
import com.okta.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class WorkflowAuthenticationStateHandler extends AuthenticationStateHandlerAdapter {

    private Logger log = LoggerFactory.getLogger(WorkflowAuthenticationStateHandler.class);

    @Autowired
    private AuthorizationServerEndpointsConfiguration configuration;

    @Override
    public void handleUnknown(AuthenticationResponse unknownResponse) {
        log.error("User failed authentication");
    }

    @Override
    public void handleSuccess(AuthenticationResponse successResponse) {
        log.info(successResponse.getUser().getLogin()+" authenticated successfully");
        // a user is ONLY considered authenticated if a sessionToken exists
        if (Strings.hasLength(successResponse.getSessionToken())) {
            Map<String, String> requestParameters = new HashMap<String, String>();
            Map<String, Serializable> extensionProperties = new HashMap<String, Serializable>();

            boolean approved = true;
            Set<String> responseTypes = new HashSet<String>();
            responseTypes.add("code");

            // Authorities
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            /*for(Role role: successResponse.get)
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            */
            OAuth2Request oauth2Request = new OAuth2Request(requestParameters, "clientIdTest", authorities, approved, new HashSet<String>(), new HashSet<String>(Arrays.asList("resourceIdTest")), null, responseTypes, extensionProperties);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(successResponse.getUser().getId(), "N/A", authorities);

            OAuth2Authentication auth = new OAuth2Authentication(oauth2Request, authenticationToken);
            AuthorizationServerTokenServices tokenService = configuration.getEndpointsConfigurer().getTokenServices();
            OAuth2AccessToken accessToken = tokenService.createAccessToken(auth);

            log.info("Access token: "+accessToken.getValue());
        }
        // other state transition successful
    }
}
