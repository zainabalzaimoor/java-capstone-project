package com.findit.lostfoundsystem.controller;

import com.findit.lostfoundsystem.dto.*;
import com.findit.lostfoundsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO user) throws Exception {
        System.out.println("Calling registerUser ==> ");
         userService.register(user);
        return ResponseEntity.ok(Map.of("message", "User registered. Check email for verification."));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        try {
            userService.verifyUser(token);
            return ResponseEntity.ok(Map.of("message","Account verified successfully! You can now login.").toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO request) {
        System.out.println("Calling loginUser ==> ");
        try {
            String token = userService.login(request);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordDTO request) {
        System.out.println("Calling forgetPassword ==> ");
        userService.forgotPassword(request.email());
//        return ResponseEntity.ok("Password reset email sent successfully!");
        return ResponseEntity.ok(Map.of("message", "Reset link sent to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        System.out.println("Calling resetPassword ==> ");
        userService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message","Password reset successfully!"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO request, Authentication authentication) {
        System.out.println("Calling changePassword ==> ");

        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok(Map.of("message","Password changed successfully!"));
    }

}
