package org.example.springsecurity6wtdemo.authcenter.controller;

import lombok.RequiredArgsConstructor;
import org.example.springsecurity6wtdemo.authcenter.entities.User;
import org.example.springsecurity6wtdemo.authcenter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/resource")
@RequiredArgsConstructor
public class AuthorizationController {
    private final UserService userService;

    @GetMapping("/currentUser")
    public ResponseEntity<Principal> currentUser(Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @GetMapping("/user")
    public ResponseEntity<User> user(Principal principal) {
        return ResponseEntity.ok(userService.findUser(principal.getName()));
    }
}
