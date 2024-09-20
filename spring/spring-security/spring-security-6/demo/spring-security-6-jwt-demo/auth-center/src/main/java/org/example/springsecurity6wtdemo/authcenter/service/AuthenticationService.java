package org.example.springsecurity6wtdemo.authcenter.service;

import org.example.springsecurity6wtdemo.authcenter.dao.request.SignUpRequest;
import org.example.springsecurity6wtdemo.authcenter.dao.request.SigninRequest;
import org.example.springsecurity6wtdemo.authcenter.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);
}
