package com.innowise.authservice.service;

import com.innowise.authservice.model.UserInfo;

import java.util.List;

public interface UserInfoService {
  List<UserInfo> findAll();
  UserInfo findById(long id);
  UserInfo save(UserInfo userInfo);
}
