package com.hana.hana1pick.domain.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@RequiredArgsConstructor
@Component
public class AccIdGenerator{

    private static final SecureRandom random = new SecureRandom();

    public String generateAccId() {
        StringBuilder accId = new StringBuilder(16);
        accId.append("11347-");
        for (int i = 0; i < 7; i ++) {
            accId.append(random.nextInt(10));
        }

        return accId.toString();
    }
}