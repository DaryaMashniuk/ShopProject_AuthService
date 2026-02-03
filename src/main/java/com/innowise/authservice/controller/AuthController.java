package com.innowise.authservice.controller;

import com.innowise.authservice.model.UserInfo;
import com.innowise.authservice.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@EnableMethodSecurity
public class AuthController {

  private final UserInfoService userInfoService;

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping
  public ResponseEntity<List<UserInfo>> getAllUsers() {
    return ResponseEntity.ok().body(userInfoService.findAll());
  }

  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/{id}")
  public ResponseEntity<UserInfo> getUserById(@PathVariable long id) {
    return ResponseEntity.ok().body(userInfoService.findById(id));
  }

  @PostMapping("/register")
  public ResponseEntity<UserInfo> register(@RequestBody UserInfo userInfo) {
    return ResponseEntity.ok().body(userInfoService.save(userInfo));
  }
}
