package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.services.RegistrationService;
import org.example.web.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/registration")
public class RegistrationController {

    private Logger logger = Logger.getLogger(RegistrationController.class);
    private RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration_page";
    }

    @PostMapping("/add")
    public String add(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration_page";
        }
        if (registrationService.register(user)) {
            logger.info(user.getUsername() + " user was registered");
            return "redirect:/login";
        } else {
            logger.info("User wasn't registered");
            bindingResult.addError(new ObjectError("badUser",
                    "User already exists"));
            return "registration_page";
        }
    }
}
