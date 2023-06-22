package com.mountblue.mygoogledrive.controllers;

import com.mountblue.mygoogledrive.entities.User;
import com.mountblue.mygoogledrive.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String createUser(){
        return "newAccount";
    }

    @PostMapping("/register")
    public String createUser(Model model, @ModelAttribute User user, @RequestParam ("confirmPassword") String confirmPassword ) {
        String createdUser = userService.createUser(user, confirmPassword);
        if(createdUser.equals("created")) {
            return "login";
        }
        System.out.println(createdUser);
        model.addAttribute("createdUser", createdUser);
        return "newAccount";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

//    @PostMapping("/login")
//    public String isValid(Model model, @RequestParam("email") String email, @RequestParam("password") String password) {
//        String isVerifyUser= userService.isValidUser (email, password);
//        if(isVerifyUser.equals("verified")){
//        return "redirect:/";
//    }
//        model.addAttribute(isVerifyUser);
//             return "login";
//    }
}
