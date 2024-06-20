package com.hana.hana1pick.domain.celebrity.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CelubPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_idx")
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "celebrity_idx")
    @JsonManagedReference
    private Celebrity celebrity;

    @Column
    private String imgSrc;
}
