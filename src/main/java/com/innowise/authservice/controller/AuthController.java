package com.innowise.authservice.controller;

import com.innowise.authservice.controller.api.AuthControllerApi;
import com.innowise.authservice.model.dto.LoginRequestDto;
import com.innowise.authservice.model.dto.RefreshTokenRequest;
import com.innowise.authservice.model.dto.RegistrationDto;
import com.innowise.authservice.model.dto.TokenResponseDto;
import com.innowise.authservice.model.dto.ValidationRequestDto;
import com.innowise.authservice.model.dto.ValidationResponseDto;
import com.innowise.authservice.service.RefreshTokenService;
import com.innowise.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
@EnableMethodSecurity
public class AuthController implements AuthControllerApi {

  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  @Override
  @PostMapping("/login")
  public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
    return ResponseEntity.ok().body(authService.authenticate(loginRequestDto));
  }

  @Override
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
     return ResponseEntity.ok().body(refreshTokenService.getNewToken(refreshTokenRequest));
  }

  @Override
  @PostMapping("/validate")
  public ResponseEntity<ValidationResponseDto> validate(@RequestBody @Valid ValidationRequestDto validationRequestDto) {
    return ResponseEntity.ok().body(authService.validate(validationRequestDto));
  }

  @Override
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody @Valid RegistrationDto registrationDto) {
    authService.save(registrationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("User registered succesfully");
  }
}
