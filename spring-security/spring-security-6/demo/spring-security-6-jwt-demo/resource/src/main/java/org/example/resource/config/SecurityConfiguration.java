package org.example.resource.config;

import lombok.RequiredArgsConstructor;
import org.example.springsecurity6jwtdemo.securityfull.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
        jsr250Enabled = true,
        securedEnabled = true
)
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf(AbstractHttpConfigurer::disable)
                // 这里配置了request.anyRequest().authenticated(), 再使用方法注解@PermitAll()就不会生效了?
                .authorizeHttpRequests(request -> request
                                // 匿名接口
                                 .requestMatchers("/api/v1/no-auth/**").permitAll()
                                // 所有接口都需要 admin权限
                                // .anyRequest().hasAuthority("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


//    /**
//     * 接口能够匿名访问，并且希望这个匿名访问还不经过 Spring Security 过滤器链
//     *
//     * @author clearmind
//     */
//    @Bean
//    WebSecurityCustomizer webSecurityCustomizer() {
//        //
//        return web -> web.ignoring().requestMatchers("/api/v1/no-auth/hello");
//    }
}
