package com.innowise.authservice.service.impl;

import com.innowise.authservice.exceptions.RefreshTokenInvalidException;
import com.innowise.authservice.exceptions.UserNotFoundException;
import com.innowise.authservice.model.RefreshToken;
import com.innowise.authservice.model.UserInfo;
import com.innowise.authservice.model.UserInfoDetails;
import com.innowise.authservice.model.dto.RefreshTokenRequest;
import com.innowise.authservice.model.dto.TokenResponseDto;
import com.innowise.authservice.repository.RefreshTokenRepository;
import com.innowise.authservice.repository.UserInfoRepository;
import com.innowise.authservice.service.RefreshTokenService;
import com.innowise.authservice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserInfoRepository userInfoRepository;
  private final JwtTokenUtil jwtTokenUtil;
  private static final long refreshTokenExpireTime = 86400000;

  @Transactional
  public RefreshToken createRefreshToken(Long userId) {
    UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found with id: "+userId));
    Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUser(userId);

    refreshToken.ifPresent(token -> refreshTokenRepository.deleteById(token.getId()));

    RefreshToken newRefreshToken = RefreshToken
            .builder()
            .userInfo(userInfo)
            .token(UUID.randomUUID().toString())
            .expiryDate(new Date(System.currentTimeMillis()+refreshTokenExpireTime))
            .build();

    refreshTokenRepository.save(newRefreshToken);
    return newRefreshToken;
  }

  public TokenResponseDto getNewToken(RefreshTokenRequest refreshTokenRequest) {
    RefreshToken refreshToken = refreshTokenRepository
            .findByToken(refreshTokenRequest.getToken())
            .orElseThrow(() -> new RefreshTokenInvalidException(refreshTokenRequest.getToken()));
    if (validateRefreshToken(refreshToken)) {
      String newToken = jwtTokenUtil.generateToken( new UserInfoDetails(refreshToken.getUserInfo()));
      return TokenResponseDto.builder()
              .refreshToken(refreshToken.getToken())
              .token(newToken)
              .expiresAt(jwtTokenUtil.extractExpiration(newToken))
              .build();
    } else {
      throw new RefreshTokenInvalidException(refreshTokenRequest.getToken());
    }
  }

  public boolean validateRefreshToken(RefreshToken refreshToken) {

    if (refreshToken.getExpiryDate().before(new Date(System.currentTimeMillis()))) {
      refreshTokenRepository.delete(refreshToken);
      return false;
    }
    return true;
  }
}
