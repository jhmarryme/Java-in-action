package org.example.springsecurity6jwtdemo.securitycore;

/**
 * @author clearmind
 */
public interface JwtHttpClient {
    User loaderUser(String token);
}
