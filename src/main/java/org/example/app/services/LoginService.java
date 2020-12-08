package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.LoginForm;
import org.example.web.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private Logger logger = Logger.getLogger(LoginService.class);
    private UserRepository<User> userRepository;

    @Autowired
    public LoginService(UserRepository<User> userUserRepository) {
        this.userRepository = userUserRepository;
    }

    public boolean authenticate(LoginForm loginFrom) {
        logger.info("try auth with user-form: " + loginFrom);
        User foundUser = userRepository.retrieveOne(loginFrom.getUsername());

        return foundUser != null && foundUser.getPassword().equals(loginFrom.getPassword());
    }
}
