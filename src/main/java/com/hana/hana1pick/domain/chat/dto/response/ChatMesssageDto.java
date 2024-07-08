package com.hana.hana1pick.domain.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatMesssageDto(Long roomId, String nation, String from, String content) {
}
