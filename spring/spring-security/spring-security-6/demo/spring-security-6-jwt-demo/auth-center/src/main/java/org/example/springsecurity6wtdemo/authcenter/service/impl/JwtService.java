package org.example.springsecurity6wtdemo.authcenter.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.springsecurity6jwtdemo.securitycore.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Autowired
    private JwtHelper jwtHelper;

//    @Autowired
//    private JwtHttpClient defaultJwtHttpClient;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String extractUserName(String token) {
        return jwtHelper.extractUserName(token);
    }

    public boolean isTokenValid(String token) {
        return jwtHelper.isTokenValid(token);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(jwtHelper.getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

}
