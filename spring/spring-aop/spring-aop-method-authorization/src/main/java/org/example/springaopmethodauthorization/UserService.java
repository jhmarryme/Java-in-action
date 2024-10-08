package org.example.springaopmethodauthorization;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getCurrentUserDynamicValue() {
        // 假设这里返回用户动态值，例如根据登录用户获取值
        return "xxx"; // 例如动态值为 "xxx"
    }
}
