package com.hana.hana1pick.domain.user.service;

import com.hana.hana1pick.domain.common.dto.response.AccountResDto;
import com.hana.hana1pick.domain.common.entity.Accounts;
import com.hana.hana1pick.domain.common.repository.AccountsRepository;
import com.hana.hana1pick.domain.deposit.repository.DepositRepository;
import com.hana.hana1pick.domain.user.dto.request.PwCheckReqDto;
import com.hana.hana1pick.domain.user.dto.request.UserUpdateReqDto;
import com.hana.hana1pick.domain.user.dto.response.PwCheckResDto;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.entity.UserTrsfLimit;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.domain.user.repository.UserTrsfLimitRepository;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserTrsfLimitRepository userTrsfLimitRepository;
    private final AccountsRepository accountsRepository;
    private final RedisTemplate<String,Object> createRedisTemplate;


    public SuccessResult<PwCheckResDto> checkPw(PwCheckReqDto request) {
        String userPwCheck = userRepository.findPasswordByUserIdx(request.getUserIdx())
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
        boolean isPwValid = passwordEncoder.matches(request.getPassword(), userPwCheck);

        return success(USER_PW_CHECK_SUCCESS, new PwCheckResDto(isPwValid));
    }

//    public Boolean findByEmail(String email) {
//        return userRepository.existsByEmail(email);
//    }

    // 이메일과 프로필만으로 사용자 저장
    public User saveUserWithEmailAndProfile(String email, String profile) {
        User user = User.builder()
                .email(email)
                .profile(profile)
                .build();
        userRepository.save(user);

        // UserTrsfLimit 생성
        UserTrsfLimit userTrsfLimit = UserTrsfLimit.builder()
                .user(user)
                .build();
        userTrsfLimitRepository.save(userTrsfLimit);

        return user;
    }

    // email과 profile만 업데이트하는 메서드
    public User updateUserProfile(UUID userId, String email, String profile) {
        User user = getUserByIdx(userId);
        user.updateEmail(email);
        user.updateProfile(profile);

        return userRepository.save(user);
    }

    // 사용자 ID를 이메일로 찾는 메서드 추가
//    public UUID findUserIdByEmail(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
//        return user.getIdx();
//    }

    public User findUserByEmail(String email) {
        // TODO: 예외 수정 필요 -> null 반환
        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
                .orElse(null);
        return user;
    }

    // 사용자의 전체 계좌 목록을 조회
    public List<AccountResDto> getAllAccountsByUserId(UUID userId) {
        User user = getUserByIdx(userId);

        List<Accounts> accounts = accountsRepository.findByUserIdx(user.getIdx());
        List<AccountResDto> result = getAccountResDtoList(accounts);

        return result;
    }

    public SuccessResult<List<AccountResDto>> getAccountsByType(UUID userIdx, String type) {
        List<Accounts> accountList = accountsRepository.findByUserIdxAndType(userIdx, type);
        List<AccountResDto> result = getAccountResDtoList(accountList);

        return success(ACCOUNT_LIST_SUCCESS, result);
    }

    private User getUserByIdx(UUID userIdx) {
        return userRepository.findById(userIdx)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    private List<AccountResDto> getAccountResDtoList(List<Accounts> accountList) {
        List<AccountResDto> result = new ArrayList<>();

        for (Accounts account : accountList) {
            result.add(AccountResDto.from(account));
        }

        return result;
    }

    //사용자 정보 수정
    public SuccessResult updateUserInfo(UserUpdateReqDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        userRepository.save(user);

        // 비밀번호 암호화 처리
        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        user.updateUserInfo(request, encodedPassword);
        userRepository.save(user);

        return success(USER_UPDATE_SUCCESS);
    }
}