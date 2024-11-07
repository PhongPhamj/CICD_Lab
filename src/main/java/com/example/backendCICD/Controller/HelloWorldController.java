package com.example.backendCICD.Controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/welcome")
public class HelloWorldController {
    @GetMapping
    public String Welcome(){
        return "Hello World!";
    }

    @GetMapping(path = "/contacts")
    public ResponseEntity<List<String>> ContactUs(@RequestParam List<String> contacts){
        return new ResponseEntity<>(contacts, HttpStatusCode.valueOf(200));
    }

}
