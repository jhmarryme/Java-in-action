package org.example.springsecurity6jwtdemo.securitycore;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author clearmind
 */
@Configuration
@ComponentScan(basePackageClasses = JwtAutoConfiguration.class)
//@PropertySource(value = {"classpath:application-jwt-dev.yml"})
public class JwtAutoConfiguration {

}
