package com.xiaot.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vip")
public class HelloController {

    @RequestMapping("/1")
    public String vip1() {
        return "vip1";
    }

    @RequestMapping("/2")
    public String vip2() {
        return "vip2";
    }

    @RequestMapping("/3")
    public String vip3() {
        return "vip3";
    }

}
