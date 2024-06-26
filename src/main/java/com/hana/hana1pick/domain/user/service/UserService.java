package com.hana.hana1pick.domain.user.service;

import com.hana.hana1pick.domain.user.dto.request.PwCheckReqDto;
import com.hana.hana1pick.domain.user.dto.response.PwCheckResDto;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.USER_PW_CHECK_SUCCESS;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public SuccessResult<PwCheckResDto> checkPw(PwCheckReqDto request) {
    String userPwCheck = userRepository.findPasswordByUserIdx(request.getUserIdx())
            .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    boolean isPwValid = passwordEncoder.matches(request.getPassword(), userPwCheck);

    return success(USER_PW_CHECK_SUCCESS, new PwCheckResDto(isPwValid));
  }

  public Boolean findByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  // 이메일과 프로필만으로 사용자 저장
  public User saveUserWithEmailAndProfile(String email, String profile) {
    User user = new User(email, profile);
    return userRepository.save(user);
  }

  // email과 profile만 업데이트하는 메서드
  public User updateUserProfile(UUID userId, String email, String profile) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    user.updateEmail(email);
    user.updateProfile(profile);
    return userRepository.save(user);
  }

  // 사용자 ID를 이메일로 찾는 메서드 추가
  public UUID findUserIdByEmail(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    return user.getIdx();
  }
}