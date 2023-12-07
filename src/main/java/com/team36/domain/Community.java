package com.team36.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Community extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cno;
    @Column(nullable = false)
    private String title;
    private String content;
    private String author;
    @Builder.Default
    private int cnt=0;

}
