package com.hana.hana1pick.domain.chat.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResDto(Long chatMessageId, String from, String content, LocalDateTime chatDate) {
}
