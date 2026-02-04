package com.innowise.authservice.repository;

import com.innowise.authservice.model.UserInfo;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
  Optional<UserInfo> findByEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);

  Optional<UserInfo> findByUsername(String username);
}
