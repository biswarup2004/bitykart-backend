package com.bity.bitykart.controller;

import com.bity.bitykart.dto.OrderDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {
    @GetMapping
    public String Home(){
        return "Welcome to bity kart";
    }
}
