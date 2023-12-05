package com.team36.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pno;

    @Column(nullable = false)
    private int mid;

    @Builder.Default
    private String memberImg = "basic.jpg";

    private String intro;

    private String gitLink1;

    private String gitLink2;

    private String gitLink3;
}
