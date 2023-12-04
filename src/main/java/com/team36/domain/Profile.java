package com.team36.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity{
    @Id
    private int pno;
    @Column(nullable = false)
    private int mid;
    @Column(length = 255, nullable = true)
    private String memberImg;
    @Column(length = 255, nullable = true)
    private String intro;
    @Column(length = 255, nullable = true)
    private String gitLink1;


}
