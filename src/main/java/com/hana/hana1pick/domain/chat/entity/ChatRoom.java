package com.hana.hana1pick.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @OneToOne
    @JoinColumn(name = "account_id")
    private MoaClub club;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ChatMessage> chatMessageList = new ArrayList<>();
}
