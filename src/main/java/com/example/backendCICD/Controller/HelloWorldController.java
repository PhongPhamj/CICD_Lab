package com.example.backendCICD.Controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class HelloWorldController {
    @GetMapping
    public String Welcome(){
        return "Hello World! Test BE Pipeline 15/12/2024/ 14:25";
    }

    @GetMapping(path = "/contacts")
    public ResponseEntity<List<String>> ContactUs(@RequestParam List<String> contacts){
        return new ResponseEntity<>(contacts, HttpStatusCode.valueOf(200));
    }

}
