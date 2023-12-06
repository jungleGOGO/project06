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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="mid")
    private Member member;

    @Builder.Default
    private String memberImg = "basic.png";

    private String intro;

    private String gitLink1;

    private String gitLink2;

    private String gitLink3;

    public void change (String intro, String gitLink1, String gitLink2){
        this.intro= intro;
        this.gitLink1=gitLink1;
        this.gitLink2=gitLink2;
    }
}
