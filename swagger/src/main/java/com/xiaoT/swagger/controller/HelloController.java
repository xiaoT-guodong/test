package com.xiaoT.swagger.controller;

import com.xiaoT.swagger.pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "hello控制层")
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@ApiParam("姓名") String name) {
        return "hello " + name;
    }

    @ApiOperation("返回传入的user")
    @GetMapping("/user")
    public User user(User user) {
        return user;
    }

}
