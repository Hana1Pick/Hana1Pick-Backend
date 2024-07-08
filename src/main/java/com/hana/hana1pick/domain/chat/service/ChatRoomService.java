package com.hana.hana1pick.domain.chat.service;

import com.hana.hana1pick.domain.chat.entity.ChatRoom;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    public ChatRoom createChatRoom(MoaClub club) {
        return ChatRoom.builder()
                .club(club)
                .build();
    }
}
