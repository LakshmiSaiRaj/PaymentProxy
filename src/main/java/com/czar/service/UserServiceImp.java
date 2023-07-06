package com.czar.service;

import com.czar.entity.User;
import com.czar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository repo;

    @Override
    public List<User> getAll() {
        return repo.findAll();
    }

}