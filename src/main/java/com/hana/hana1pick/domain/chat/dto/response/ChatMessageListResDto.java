package com.hana.hana1pick.domain.chat.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageListResDto(Long chatMessageId, String from, String contentKO, String contentZH, String contentJA, LocalDateTime chatDate) {
}
