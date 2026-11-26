package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.LoginRequestDto;
import com.example.Football_League_API.dto.request.RegistroRequestDto;
import com.example.Football_League_API.dto.response.AuthResponseDto;
import com.example.Football_League_API.dto.response.UsuarioResponseDto;
import com.example.Football_League_API.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registrar(@Valid @RequestBody RegistroRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(service.login(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDto> me() {
        return ResponseEntity.ok(service.me());
    }
}
