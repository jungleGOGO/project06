package com.team36.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityDTO {

    private Long cno;
    private String title;
    private String content;
    private String author;
    @Builder.Default
    private int cnt = 0;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
