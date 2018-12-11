package com.okta.spring.example;

import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.factor.Factor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(value="session", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class Registration implements Serializable {
    private String userid;
    private User user;
    private char[] password;
    private Factor emailFactor;

    public Registration() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Factor getEmailFactor() {
        return emailFactor;
    }

    public void setEmailFactor(Factor emailFactor) {
        this.emailFactor = emailFactor;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
