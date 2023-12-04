package com.team36.dto;

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
    private int pno;
    @JoinColumn(name = "mid", referencedColumnName = "mid")
    private int mid;
    private String memberImg;
    private String intro;
    private String gitLink1;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
