package com.team36.service;

import com.team36.domain.Member;
import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.ProfileDTO;
import org.springframework.data.repository.query.Param;

public interface ProfileService {
    int insertProfile(ProfileDTO profileDTO);

    void updateProfile(ProfileDTO profileDTO);

    Profile existsProfileByMember(Member member);
}
