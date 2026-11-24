package com.hospital.management.controller;

import com.hospital.management.dto.ApiResponse;
import com.hospital.management.dto.LoginRequest;
import com.hospital.management.dto.LoginResponse;
import com.hospital.management.dto.SignupRequest;
import com.hospital.management.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate a user and return a JWT token")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    @Operation(summary = "User signup", description = "Register a new user account")
    public ResponseEntity<ApiResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        ApiResponse apiResponse = authService.signup(signupRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
