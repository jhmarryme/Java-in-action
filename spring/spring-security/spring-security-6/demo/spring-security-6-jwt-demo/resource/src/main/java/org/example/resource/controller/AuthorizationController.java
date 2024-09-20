package org.example.resource.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/resource")
@RequiredArgsConstructor
public class AuthorizationController {

    @GetMapping("sayHello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Here is your resource");
    }


    @GetMapping("/currentUser")
    public ResponseEntity<Principal> currentUser(Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @GetMapping("/currentAdminUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Principal> currentAdminUser(Principal principal) {
        return ResponseEntity.ok(principal);
    }

}
