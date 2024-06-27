package com.hana.hana1pick.domain.user.service;

import com.hana.hana1pick.domain.user.dto.request.PwCheckReqDto;
import com.hana.hana1pick.domain.user.dto.request.UserCreateReqDto;
import com.hana.hana1pick.domain.user.dto.response.PwCheckResDto;
import com.hana.hana1pick.domain.user.dto.response.UserCreateResDto;
import com.hana.hana1pick.domain.user.entity.User;
import com.hana.hana1pick.domain.user.repository.UserRepository;
import com.hana.hana1pick.global.exception.BaseException;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.USER_PW_CHECK_SUCCESS;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.REQUEST_ERROR;

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

    public UserCreateResDto save(UserCreateReqDto userCreateReqDto) {
        try {
            User saved = userRepository.save(userCreateReqDto.toEntity());
            return new UserCreateResDto(saved.getIdx());
        } catch (DataIntegrityViolationException e) {
            throw new BaseException(REQUEST_ERROR);
        }
    }
}
