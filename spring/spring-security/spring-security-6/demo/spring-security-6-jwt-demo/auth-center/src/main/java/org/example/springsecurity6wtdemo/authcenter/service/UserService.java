package org.example.springsecurity6wtdemo.authcenter.service;

import org.example.springsecurity6wtdemo.authcenter.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    User findUser(String username);
}
