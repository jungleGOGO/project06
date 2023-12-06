package com.team36.service;

import com.team36.domain.Member;
import com.team36.dto.MemberJoinDTO;
import org.modelmapper.internal.Errors;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface MemberService {
    static class MidExistException extends Exception {}
    Member existByEmail(String email);
    void join(MemberJoinDTO memberJoinDTO) ;

    void changePw(MemberJoinDTO memberJoinDTO);


}