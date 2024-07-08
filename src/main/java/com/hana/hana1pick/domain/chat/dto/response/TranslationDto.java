package com.hana.hana1pick.domain.chat.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslationDto {
    private String detected_source_language;
    private String text;

    @Override
    public String toString() {
        return "TranslationDto{" +
                "text='" + text + '\'' +
                '}';
    }
}