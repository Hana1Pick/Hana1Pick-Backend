package com.hana.hana1pick.domain.moaclub.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public interface VoteResult {
    String getAccountId();
    String getUserName();
    LocalDateTime getRequestTime();
    Map<String, Boolean> getVotes();
}
