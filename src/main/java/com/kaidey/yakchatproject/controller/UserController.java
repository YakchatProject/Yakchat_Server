package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.UserDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.service.UserService;
import com.kaidey.yakchatproject.util.CookieUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final CookieUtil cookieUtil;

    @Autowired
    public UserController(UserService userService, CookieUtil cookieUtil) {
        this.userService = userService;
        this.cookieUtil = cookieUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            User newUser = userService.registerUser(userDto);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Username already exists")) {
                return ResponseEntity.status(409).body("Username already exists");
            }
            return ResponseEntity.status(500).body("Error registering user: " + e.getMessage());
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDto userDto) {
//        try {
//            Map<String, String> tokens = userService.loginUser(userDto);
//            return ResponseEntity.ok(tokens);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(403).body(null);
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto, HttpServletResponse response, HttpServletRequest request) {
        try {
            Map<String, String> tokens = userService.loginUser(userDto);

            cookieUtil.addAccessToken(response, tokens.get("access_token"), request);
            cookieUtil.addRefreshToken(response, tokens.get("refresh_token"));

            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid username or password"));
        }
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
//        try {
//            String refreshToken = request.get("refreshToken");
//            Map<String, String> tokens = userService.refreshToken(refreshToken);
//            return ResponseEntity.ok(tokens);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(403).body(null);
//        }
//    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = cookieUtil.getRefreshToken(request)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            Map<String, String> tokens = userService.refreshToken(refreshToken);

            // 기존 access_token 제거 후 새 토큰 저장
            cookieUtil.addAccessToken(response, tokens.get("access_token"), request);
            cookieUtil.addRefreshToken(response, tokens.get("refresh_token"));

            return ResponseEntity.ok(Map.of("message", "Token refreshed"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        cookieUtil.deleteAccessToken(response);
        cookieUtil.deleteRefreshToken(response);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }


    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}