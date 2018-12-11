package com.okta.spring.example.controllers;

import com.okta.authn.sdk.resource.AuthenticationResponse;
import com.okta.spring.example.OktaAdmin;
import com.okta.spring.example.OktaAuth;
import com.okta.spring.example.Registration;
import com.okta.spring.example.models.RegisteringUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RegisterController {

    @Autowired
    private OktaAdmin oktaAdmin;
    @Autowired
    private OktaAuth oktaAuth;

    @GetMapping
    public ModelAndView getRegister(){
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("identifier",new RegisteringUser());
        return mav;
    }

    @Autowired
    Registration reg;

    @PostMapping("/register")
    public ModelAndView registerSubmit(@ModelAttribute RegisteringUser user){
        ModelAndView mav = new ModelAndView("register");

        switch (user.getWorkflowStage()){
            case prereg:
                if (oktaAdmin.userIsRegistered(user)) {
                    //redirect the user to login
                    //TODO explanation would be nice
                    mav.setViewName("login");
                    return mav;
                }
                if(user.getUserIdentifier().contains("@")){
                    user.setEmail(user.getUserIdentifier());
                }
                user.setWorkflowStage(RegisteringUser.WorkflowStage.enroll);
                break;
            case enroll:
                reg.setUser(oktaAdmin.registerUser(user));
                reg.setEmailFactor(oktaAdmin.EnrollEmail(user, reg.getUser()));
                reg.setPassword(user.getPassword());
                user.setWorkflowStage(RegisteringUser.WorkflowStage.verify);
                break;
            case verify:
                oktaAdmin.verifyEmail(reg.getEmailFactor(), user.getVerificationCode());
                user.setWorkflowStage(RegisteringUser.WorkflowStage.welcome);
        }
        mav.addObject("identifier",user);
        return mav;
    }
}
