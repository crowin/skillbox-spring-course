package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepository implements UserRepository<User> {

    private final Logger logger = Logger.getLogger(CustomerRepository.class);
    private final List<User> repo = new ArrayList<>();

    public CustomerRepository() {
        User user = new User();
        user.setId(1);
        user.setUsername("root");
        user.setPassword("123");
        repo.add(user);
    }

    @Override
    public List<User> retrieveAll() {
        return new ArrayList<>(repo);
    }

    @Override
    public User retrieveOne(String userName) {
        return repo.stream().filter(u -> u.getUsername().equals(userName)).findFirst().orElse(null);
    }

    @Override
    public void store(User user) {
        user.setId(user.hashCode());
        logger.info("store new user: " + user);
        repo.add(user);
    }
}
