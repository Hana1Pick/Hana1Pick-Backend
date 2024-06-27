package com.hana.hana1pick.domain.celublog.dto.request;

import com.hana.hana1pick.domain.celublog.entity.Rules;
import lombok.*;

import java.util.List;

@Builder
@Getter
public class AddRuleReqDto {
    private String accountId;
    private List<Rule> ruleList;
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Rule{
        private String ruleName;
        private long ruleMoney;
    }
}
