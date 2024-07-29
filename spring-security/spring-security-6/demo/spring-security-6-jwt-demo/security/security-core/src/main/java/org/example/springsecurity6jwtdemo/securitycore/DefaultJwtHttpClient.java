package org.example.springsecurity6jwtdemo.securitycore;

import cn.hutool.http.HttpUtil;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author clearmind
 */
//@ConditionalOnMissingBean(JwtHttpClient.class)
@Component
public class DefaultJwtHttpClient implements JwtHttpClient{

    @Value("${user-url}")
    private String userUrl;

    @Override
    public User loaderUser(String token) {
        String body = HttpUtil.createGet(userUrl).bearerAuth(token).execute().body();
        return new GsonBuilder().setPrettyPrinting().create().fromJson(body, User.class);
    }
}
