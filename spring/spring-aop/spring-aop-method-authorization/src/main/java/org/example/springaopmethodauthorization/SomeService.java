package org.example.springaopmethodauthorization;

import org.example.springaopmethodauthorization.valid.ValidateMethodField;
import org.springframework.stereotype.Service;

@Service
public class SomeService {

    @ValidateMethodField(fieldName = "targetField", message = "目标字段值不匹配")
    public void someMethod(SomeClass param, Integer otherParam) {
        // 方法逻辑
        System.out.println("执行 someMethod");
    }
}
