package com.hana.hana1pick.domain.chat.service;

import com.hana.hana1pick.domain.chat.dto.response.*;
import com.hana.hana1pick.domain.chat.entity.ChatMessage;
import com.hana.hana1pick.domain.chat.entity.ChatRoom;
import com.hana.hana1pick.domain.chat.repository.ChatMessageRepository;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.CHAT_MESSAGE_CREATED_SUCCESS;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.CHAT_MESSAGE_LIST_LOAD_SUCCESS;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    @Value("${app.deepl.apiURL}")
    private String deeplApiUrl;

    @Value("${app.deepl.secretKey}")
    private String secretKey;

    private final ChatMessageRepository chatMessageRepository;

    public BaseResponse.SuccessResult<ChatMessageResDto> createChatMessage(ChatMesssageDto request){
        LocalDateTime chatDate = LocalDateTime.now();

        String contentKO = translate(new TranslationReqDto(Collections.singletonList(request.content()), "KO")).getTranslations().get(0).getText();
        String contentZH = translate(new TranslationReqDto(Collections.singletonList(request.content()), "ZH")).getTranslations().get(0).getText();
        String contentJA = translate(new TranslationReqDto(Collections.singletonList(request.content()), "JA")).getTranslations().get(0).getText();

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(ChatRoom.builder().chatRoomId(request.roomId()).build())
                .contentKorea(contentKO)
                .contentChina(contentZH)
                .contentJapan(contentJA)
                .writer(request.from())
                .chatDate(chatDate)
                .build();

        Long chatMessageId = chatMessageRepository.save(chatMessage).getChatMessageId();

        String content = switch (request.nation()) {
            case "Korea" -> contentKO;
            case "China" -> contentZH;
            default -> contentJA; // Japan
        };

        return success(CHAT_MESSAGE_CREATED_SUCCESS, new ChatMessageResDto(chatMessageId, request.from(), content, chatDate));
    }

    public BaseResponse.SuccessResult<ChatRoomResDto> getChatMessage(Long roomId, String nation, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> chatMessagesPage = chatMessageRepository.findByChatRoomId(roomId, pageable);

        List<ChatMessageResDto> messageList = chatMessagesPage.getContent().stream()
                .map(chatMessage -> ChatMessageResDto.builder()
                        .chatMessageId(chatMessage.getChatMessageId())
                        .from(chatMessage.getWriter())
                        .content(getContentByNation(nation, chatMessage))
                        .chatDate(chatMessage.getChatDate())
                        .build())
                .collect(Collectors.toList());

        ChatRoomResDto chatRoomResDto = ChatRoomResDto.builder()
                .messageList(messageList)
                .build();

        return success(CHAT_MESSAGE_LIST_LOAD_SUCCESS, chatRoomResDto);
    }

    public String getContentByNation(String nation, ChatMessage chatMessage){
        return switch (nation) {
            case "Korea" -> chatMessage.getContentKorea();
            case "China" -> chatMessage.getContentChina();
            default -> chatMessage.getContentJapan(); // Japan
        };
    }

    public TranslationResDto translate(@RequestBody TranslationReqDto request) {
        String targetLang = request.getTarget_lang();

        String apiUrl = deeplApiUrl + "?target_lang=" + targetLang;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "DeepL-Auth-Key " + secretKey);

        TranslationResDto response = restTemplate.postForObject(apiUrl, createHttpEntity(request, headers), TranslationResDto.class);

        return response;
    }

    private HttpEntity<TranslationReqDto> createHttpEntity(TranslationReqDto request, HttpHeaders headers) {
        return new HttpEntity<>(request, headers);
    }
}
