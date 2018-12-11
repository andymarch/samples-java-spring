package com.okta.spring.example.controllers;

import com.okta.spring.example.OktaAdmin;
import com.okta.spring.example.models.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private OktaAdmin admin;

    @GetMapping("/")
    public ModelAndView home(Principal user) {
        ModelAndView mav = new ModelAndView("home");
        if(user != null){
            RegisteredUser ru = admin.getRegisteredUser(user.getName());
            mav.addObject("registeredUser",ru);
        }
        return mav;
    }


    @PostMapping("/updateprofile")
    public RedirectView profile(@ModelAttribute RegisteredUser identifier){
        admin.updateUser(identifier);
        return new RedirectView("/");
    }
}