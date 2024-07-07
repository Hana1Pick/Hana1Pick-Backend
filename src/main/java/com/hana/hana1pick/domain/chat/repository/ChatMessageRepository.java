package com.hana.hana1pick.domain.chat.repository;

import com.hana.hana1pick.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query(value="SELECT * FROM chat_message WHERE chat_room_id=:roomId",  nativeQuery = true)
    Page<ChatMessage> findByChatRoomId(Long roomId, Pageable pageable);
}
