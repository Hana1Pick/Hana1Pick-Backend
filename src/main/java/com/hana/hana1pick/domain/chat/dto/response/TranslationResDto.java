package com.hana.hana1pick.domain.chat.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TranslationResDto {
    private List<TranslationDto> translations;

    @Override
    public String toString() {
        return "TranslationResDto{" +
                "translations=" + translations +
                '}';
    }
}