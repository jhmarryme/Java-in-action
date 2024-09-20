package org.example.resource.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 需要先开启 EnableMethodSecurity
 *
 * @author clearmind
 */
@RestController
@RequestMapping("/api/v1/resource/method-security")
public class MethodSecurityController {


    @GetMapping("/readAuth")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Principal> readAuth(Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @GetMapping("/writeAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Principal> writeAuth(Principal principal) {
        // 因为没配置admin的权限, 所以这里应该是访问不到的
        return ResponseEntity.ok(principal);
    }
}
