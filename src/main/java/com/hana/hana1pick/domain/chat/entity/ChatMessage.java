package com.hana.hana1pick.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    private String contentKorea;

    private String contentChina;

    private String contentJapan;

    private String writer;

    private LocalDateTime chatDate;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    @JsonManagedReference
    private ChatRoom chatRoom;
}
