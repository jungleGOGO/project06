package com.team36.service;

import com.team36.domain.Member;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.MemberSecurityDTO;
import org.modelmapper.internal.Errors;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface MemberService {
    static class MidExistException extends Exception {}
    Member existByEmail(String email);
    void join(MemberJoinDTO memberJoinDTO) ;
    void changePw(MemberJoinDTO memberJoinDTO);
    boolean changeName(MemberJoinDTO memberJoinDTO);
    List<MemberJoinDTO> list();
    void changeActive (Integer active, Integer mid);
}