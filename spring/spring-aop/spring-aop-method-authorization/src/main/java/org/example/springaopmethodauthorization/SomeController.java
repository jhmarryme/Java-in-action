package org.example.springaopmethodauthorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SomeController {

    @Autowired
    private SomeService someService;

    @GetMapping("/test")
    public String testMethod(String id) {
        SomeClass param = new SomeClass(id); // 这个值应该与动态值匹配
        someService.someMethod(param, 0); // 触发校验
        return "校验通过";
    }
}
