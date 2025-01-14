package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.UserDto;
import com.kaidey.yakchatproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        UserDto registeredUser = userService.registerUser(userDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        Optional<String> jwtToken = userService.login(username, password);
        return jwtToken.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body("로그인 실패")); // 로그인 실패 시
    }
}
