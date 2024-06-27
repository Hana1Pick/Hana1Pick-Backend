package com.hana.hana1pick.domain.moaclub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ManagerChangeReq implements VoteResult, Serializable {

    private String accountId;
    private String userName;
    private String candidateName;
    private LocalDateTime requestTime;
    private Map<String, Boolean> votes;
}
