package com.hana.hana1pick.domain.celublog.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AddRuleResDto {
    private List<Rule> ruleList;
    public class Rule{
        private String ruleName;
        private long ruleMoney;
    }
}
