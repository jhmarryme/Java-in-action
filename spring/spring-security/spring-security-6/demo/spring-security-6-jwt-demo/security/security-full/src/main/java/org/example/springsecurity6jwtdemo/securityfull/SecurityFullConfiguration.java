package org.example.springsecurity6jwtdemo.securityfull;

import org.example.springsecurity6jwtdemo.securitycore.JwtAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author clearmind
 */
@Configuration
@Import(JwtAutoConfiguration.class)
@ComponentScan(basePackageClasses = SecurityFullConfiguration.class)
public class SecurityFullConfiguration {
}
