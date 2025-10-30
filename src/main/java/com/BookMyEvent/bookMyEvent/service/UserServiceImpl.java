package com.BookMyEvent.bookMyEvent.service;


import com.BookMyEvent.bookMyEvent.entity.User;
import com.BookMyEvent.bookMyEvent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public User createUser(User user){
//        user.setPassword(encoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }

    public User createUser(User user){
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

//     private BCryptPasswordEncoder encoder;

//    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
//        this.userRepository = userRepository;
//        this.encoder = encoder;
//    }


}
