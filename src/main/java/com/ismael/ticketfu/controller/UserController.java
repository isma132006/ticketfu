package com.ismael.ticketfu.controller;


import com.ismael.ticketfu.dto.request.RegisterRequestDto;
import com.ismael.ticketfu.dto.response.UserResponse;
import com.ismael.ticketfu.entity.Users;
import com.ismael.ticketfu.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {


    private final  UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody RegisterRequestDto request) {

        UserResponse response = service.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        return service.verificar(user);
    }

    @GetMapping("/hola")
    public String hola() {
        return "Hola";
    }
}