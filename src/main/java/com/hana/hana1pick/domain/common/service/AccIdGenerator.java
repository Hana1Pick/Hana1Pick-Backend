package com.hana.hana1pick.domain.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@RequiredArgsConstructor
@Component
public class AccIdGenerator{

    private static final SecureRandom random = new SecureRandom();
    private static final int ACC_LENGTH = 7;

    public String generateDepositAccId() {
        return generateAccId("02-00-");
    }

    public String generateCelublogAccId() {
        return generateAccId("02-01-");
    }

    public String generateMoaClubAccId() {
        return generateAccId("02-02-");
    }

    public String generateAccId(String prefix) {
        StringBuilder accId = new StringBuilder(prefix);
        for (int i = 0; i < ACC_LENGTH; i ++) {
            accId.append(random.nextInt(10));
        }

        return accId.toString();
    }
}