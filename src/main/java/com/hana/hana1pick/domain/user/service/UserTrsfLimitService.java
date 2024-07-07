package com.hana.hana1pick.domain.user.service;

import com.hana.hana1pick.domain.user.entity.UserTrsfLimit;
import com.hana.hana1pick.domain.user.repository.UserTrsfLimitRepository;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponseStatus.USER_TRSF_LIMIT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserTrsfLimitService {

    private final UserTrsfLimitRepository userTrsfLimitRepository;

    public void accumulate(UUID userIdx, Long amount) {
        UserTrsfLimit userTrsfLimit = findUserTrsfLimitByUserCode(userIdx);
        userTrsfLimit.accumulate(amount);
    }

    public boolean checkTrsfLimit(UUID userIdx, Long amount) {
        UserTrsfLimit userTrsfLimit = findUserTrsfLimitByUserCode(userIdx);
        if (userTrsfLimit.getDailyAmount() + amount > userTrsfLimit.getTransferLimit()) {
            return true;
        }
        return false;
    }

    public void resetUserDailyTrsfLimit() {
        List<UserTrsfLimit> userTrsfLimitList = userTrsfLimitRepository.findAll();
        for (UserTrsfLimit userTrsfLimit : userTrsfLimitList) {
            userTrsfLimit.resetDailyAccAmount();
        }
    }

    private UserTrsfLimit findUserTrsfLimitByUserCode(UUID userIdx) {
        UserTrsfLimit userTrsfLimit = userTrsfLimitRepository.findById(userIdx)
                .orElseThrow(() -> new BaseException(USER_TRSF_LIMIT_NOT_FOUND));

        return userTrsfLimit;
    }
}
