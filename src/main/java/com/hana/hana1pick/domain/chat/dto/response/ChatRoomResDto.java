package com.hana.hana1pick.domain.chat.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatRoomResDto {
    private List<ChatMessageResDto> messageList;
}
