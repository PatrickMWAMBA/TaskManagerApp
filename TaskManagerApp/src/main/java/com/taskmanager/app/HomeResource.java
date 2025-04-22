package com.taskmanager.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeResource {

    //@Value("${environment}")
   // private String environment;

    @GetMapping("/")
    public String index() {
        return "Welcome to  Task Manager App under construction.";
    }

}
