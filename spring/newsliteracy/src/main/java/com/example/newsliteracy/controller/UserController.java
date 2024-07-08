package com.example.newsliteracy.controller;

import com.example.newsliteracy.model.User;
import com.example.newsliteracy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        logger.info("Attempting to register user with username: {}", username);
        if (userRepository.findByUsername(username) != null) {
            logger.warn("Username {} already exists.", username);
            model.addAttribute("error", "Username already exists.");
            return "register";
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        logger.info("User {} registered successfully with encoded password {}.", username, encodedPassword);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        logger.info("Accessing login form");
        return "login";
    }
}
