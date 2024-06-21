package com.hana.hana1pick.domain.celebrity.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.celublog.entity.Celublog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Celebrity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "celebrity_idx")
    private Long idx;

    @Column
    private String name;

    @Column
    private CelubType type;

    @Column
    private String thumbnail;

    @OneToMany(mappedBy = "celebrity")
    @JsonManagedReference
    private List<CelubPhoto> photoList = new ArrayList<>();

    @OneToMany(mappedBy = "celebrity")
    @JsonManagedReference
    private List<Celublog> celublogList = new ArrayList<>();
}
