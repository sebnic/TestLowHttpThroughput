package com.seb.TestLowHttpThroughput;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpEndpoint {

    @GetMapping("/message")
    public String getMessage() {
        return "Mon message";
    }
}
