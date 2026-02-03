package com.innowise.authservice.service.impl;

import com.innowise.authservice.model.UserInfo;
import com.innowise.authservice.repository.UserInfoRepository;
import com.innowise.authservice.service.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserInfoServiceImpl implements UserInfoService {

  private BCryptPasswordEncoder bCryptPasswordEncoder;

  private UserInfoRepository userInfoRepository;

  @Override
  public List<UserInfo> findAll() {
    return userInfoRepository.findAll();
  }

  @Override
  public UserInfo findById(long id) {
    return userInfoRepository.findById(id).orElseThrow();
  }

  @Override
  public UserInfo save(UserInfo userInfo) {
    userInfo.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
    return userInfoRepository.save(userInfo);
  }
}
