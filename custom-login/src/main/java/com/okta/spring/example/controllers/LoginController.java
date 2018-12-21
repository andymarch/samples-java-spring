/*
 * Copyright 2017 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.spring.example.controllers;

import com.okta.spring.config.OktaClientProperties;
import com.okta.spring.config.OktaOAuth2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String STATE = "state";
    private static final String SCOPES = "scopes";
    private static final String OKTA_BASE_URL = "oktaBaseUrl";
    private static final String OKTA_CLIENT_ID = "oktaClientId";
    private static final String REDIRECT_URI = "redirectUri";
    private static final String ISSUER_URI = "issuerUri";

    private final OktaOAuth2Properties oktaOAuth2Properties;
    private final OktaClientProperties oktaClientProperties;
    private final UserInfoRestTemplateFactory templateFactory;
    @Autowired
    Environment environment;


    public LoginController(OktaOAuth2Properties oktaOAuth2Properties, OktaClientProperties oktaClientProperties,UserInfoRestTemplateFactory templateFactory) {
        this.templateFactory = templateFactory;
        this.oktaOAuth2Properties = oktaOAuth2Properties;
        this.oktaClientProperties = oktaClientProperties;
    }

    @GetMapping(value = "/login")
    public ModelAndView login(HttpServletRequest request,
                              @RequestParam(name = "state", required = false) String state) {

        // if we don't have the state parameter redirect
        if (state == null) {
            return new ModelAndView("redirect:" + oktaOAuth2Properties.getRedirectUri());
        }

        // configuration for Okta Signin Widget
        ModelAndView mav = new ModelAndView("login");
        mav.addObject(STATE, state);
        mav.addObject(SCOPES, oktaOAuth2Properties.getScopes());
        mav.addObject(OKTA_BASE_URL, oktaClientProperties.getOrgUrl());
        mav.addObject(OKTA_CLIENT_ID, oktaOAuth2Properties.getClientId());
        mav.addObject(REDIRECT_URI,
            request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + oktaOAuth2Properties.getRedirectUri()
        );
        mav.addObject(ISSUER_URI, oktaOAuth2Properties.getIssuer());
        return mav;
    }

    @PostMapping("/globalLogout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        OAuth2RestTemplate oauth2RestTemplate = this.templateFactory.getUserInfoRestTemplate();
        String idToken = (String) oauth2RestTemplate.getAccessToken().getAdditionalInformation().get("id_token");

        //redirect the user view Okta to logout the user's Okta session
        String redirectString = oktaOAuth2Properties.getIssuer() + "/v1/logout?id_token_hint=" + idToken;

        //the post_logout_redirect_uri will return the user to application after logout has been completed
        try {
            redirectString = redirectString+"&post_logout_redirect_uri=" +
                    URLEncoder.encode("http://localhost:" + environment.getProperty("local.server.port") + "/",
                            "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("Failed to parse redirect uri");
        }

        //logout the local spring session
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        //redirect the user to Okta
        return new ModelAndView("redirect:" + redirectString);
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}