package com.okta.spring.example;

import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;
import com.okta.sdk.resource.user.UserList;
import com.okta.sdk.resource.user.factor.EmailFactor;
import com.okta.sdk.resource.user.factor.Factor;
import com.okta.sdk.resource.user.factor.VerifyFactorRequest;
import com.okta.spring.example.models.RegisteredUser;
import com.okta.spring.example.models.RegisteringUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class OktaAdmin {

    private static final Logger log = LoggerFactory.getLogger(OktaAdmin.class);

    private Client client = Clients.builder()
            .setOrgUrl("https://examply.okta-emea.com/")
            //TODO inject this
            .setClientCredentials(
                    new TokenClientCredentials("00QQrOzvUE5t4xgPe8OpTUpZe_7JZK9k7ZDPMlXnZ6"))
            .build();

    public Client getClient() {
        return client;
    }

    public boolean userIsRegistered(RegisteringUser id){
        UserList list;
        list = client.listUsers(id.getUserIdentifier(), null, null, null, null);
        if(list.stream().count() != 0){
            log.info("Found a registered user with identifier "+id.getUserIdentifier());
            return true;
        }
        log.info("No registered user with identifier "+id.getUserIdentifier());
        return false;
    }

    public RegisteredUser getRegisteredUser(String id){
        User user = client.getUser(id);
        RegisteredUser ru = new RegisteredUser();
        ru.setId(user.getId());
        ru.setFirstName(user.getProfile().getFirstName());
        ru.setLastName(user.getProfile().getLastName());
        ru.setCountry(user.getProfile().getString("Country"));
        Object value = user.getProfile().get("MarketingOptOut");
        if(value != null) {
            ru.setMarketingOptOut((boolean)value);
        }
        return ru;
    }

    public User registerUser(RegisteringUser id) {
        User user = UserBuilder.instance()
                .setLogin(id.getUserIdentifier())
                .setEmail(id.getEmail())
                .setPassword(id.getPassword())
                .buildAndCreate(client);
        log.info("User created");
        return user;
    }

    public Factor EnrollEmail(RegisteringUser id, User user){
            log.debug(user.listFactors().stream().count()+" factor enrolled.");
            EmailFactor factor = client.instantiate(EmailFactor.class);
            factor.getProfile().setEmail(id.getEmail());
            Factor emailFactor = user.addFactor(factor);
            log.info("User enrolled for email factor");
            log.debug(user.listFactors().stream().count()+" factors enrolled.");
            return emailFactor;
    }

    public boolean verifyEmail(Factor emailFactor,String verification) {
        VerifyFactorRequest verifyFactorRequest = client.instantiate(VerifyFactorRequest.class);
        verifyFactorRequest.setPassCode(verification);
        emailFactor.activate(verifyFactorRequest);
        return true;
    }

    public void updateUser(RegisteredUser registeredUser) {
        User user = client.getUser(registeredUser.getId());
        user.getProfile().setFirstName(registeredUser.getFirstName());
        user.getProfile().setLastName(registeredUser.getLastName());
        user.getProfile().put("Country",registeredUser.getCountry());
        user.getProfile().put("MarketingOptOut",registeredUser.isMarketingOptOut());
        user.update();
    }
}
