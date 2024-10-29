package org.example.bucket4j_caffeine_ratelimiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author clearmind
 */
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "success";
    }
}
