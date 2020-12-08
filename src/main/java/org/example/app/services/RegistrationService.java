package org.example.app.services;

import org.example.web.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private UserRepository<User> userRepository;

    @Autowired
    public RegistrationService(UserRepository<User> userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(User user) {
        if (userRepository.retrieveOne(user.getUsername()) != null) return false;
        userRepository.store(user);
        return true;
    }
}
