package com.hana.hana1pick.domain.chat.controller;

import com.hana.hana1pick.domain.chat.dto.request.ChatMessageReqDto;
import com.hana.hana1pick.domain.chat.dto.response.ChatMessageResDto;
import com.hana.hana1pick.domain.chat.dto.response.ChatMesssageDto;
import com.hana.hana1pick.domain.chat.dto.response.ChatRoomResDto;
import com.hana.hana1pick.domain.chat.service.ChatMessageService;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/api/chat/{roomId}")
    public BaseResponse.SuccessResult<ChatRoomResDto> getChatRoom(@PathVariable Long roomId,
                                                                  @RequestParam String nation,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "100") int size){
        return chatMessageService.getChatMessage(roomId, nation, page, size);
    }

    @MessageMapping("/chat/{roomId}/send")
    @SendTo("/topic/public/rooms/{roomId}")
    public BaseResponse.SuccessResult<ChatMessageResDto> sendMessage(@DestinationVariable Long roomId,
                                                                     @Payload ChatMessageReqDto request) {
        ChatMesssageDto chatMessage = ChatMesssageDto.builder()
                .roomId(roomId)
                .nation(request.nation())
                .from(request.from())
                .content(request.text())
                .build();

        return chatMessageService.createChatMessage(chatMessage);
    }
}
