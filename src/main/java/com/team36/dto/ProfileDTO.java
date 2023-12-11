package com.team36.dto;

import com.team36.domain.Member;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;
    private int mid;
    private Member member;
    private String memberImg = "basic.png";
    private String intro;
    private String gitLink1;
    private String gitLink2;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
