package com.team36.dto;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;


@Getter
@Setter
public class MemberJoinDTO {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mid;
    
    @NotBlank(message ="**")
    private String mpw;

    @NotBlank(message = "**")
    private String mname;

    @NotBlank(message = "**")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotBlank(message = "**")
    private String code;

    @Column(name = "active", columnDefinition = "integer default 0")
    private Integer active = 0;

    private LocalDateTime regDate;
    private String nowPassword;
    private String passwordConfirm;

}