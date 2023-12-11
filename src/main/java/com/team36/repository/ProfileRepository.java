package com.team36.repository;

import com.team36.domain.Member;
import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("select p from Profile p where p.member =:member")
    Profile existsProfileByMember(Member member);



}
