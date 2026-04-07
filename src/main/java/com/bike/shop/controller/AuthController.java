package com.bike.shop.controller;

import com.bike.shop.dto.request.LoginRequestDTO;
import com.bike.shop.dto.response.LoginResponseDTO;
import com.bike.shop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "https://bike-shop-frontend-production.up.railway.app")
public class AuthController {

    private final AuthService authService;

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
