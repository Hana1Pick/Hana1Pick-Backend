package com.hana.hana1pick.domain.chat.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TranslationReqDto {
    private List<String> text;
    private String target_lang;

    public TranslationReqDto(List<String> text, String target_lang) {
        this.text = text;
        this.target_lang = target_lang;
    }
}