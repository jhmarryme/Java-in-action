package org.example.springsecurity6jwtdemo.securitycore;

import java.security.Key;

/**
 * @author clearmind
 */
public interface JwtHelper {
    String extractUserName(String token);
    boolean isTokenValid(String token);

    Key getSigningKey();
}
