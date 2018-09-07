package com.carshop.carshop.web.controllers;

import com.carshop.carshop.domain.models.binding.UserRegisterBindingModel;
import com.carshop.carshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", produces = "application/json")
    public Map<String, String> register(@RequestBody UserRegisterBindingModel bindingModel) {
        Map<String, String> map = new HashMap<>();

        map.put("message", "success");

        if (bindingModel.getUsername().equals("") || bindingModel.getPassword().equals("") || bindingModel.getConfirmPassword().equals("")
                || bindingModel.getEmail().equals("")) {
            map.put("message", "Please fill in all fields.");
            return map;
        }

        if (!bindingModel.getPassword().equals(bindingModel.getConfirmPassword())) {
            map.put("message", "Passwords do not match.");
            return map;
        }

        if (this.userService.findByEmail(bindingModel.getEmail()) != null) {
            map.put("message", "Email is already taken.");
            return map;
        }

        if (this.userService.findByUsername(bindingModel.getUsername()) != null) {
            map.put("message", "Username is already taken.");
            return map;
        }

        this.userService.register(bindingModel);

        return map;
    }
}
