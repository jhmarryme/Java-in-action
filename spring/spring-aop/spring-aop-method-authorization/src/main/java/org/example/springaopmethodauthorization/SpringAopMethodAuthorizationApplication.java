package org.example.springaopmethodauthorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class SpringAopMethodAuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAopMethodAuthorizationApplication.class, args);
    }

}
