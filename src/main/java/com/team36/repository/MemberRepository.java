package com.team36.repository;

import com.team36.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String>, Search{

    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.email = :email")
    Optional<Member> getWithRoles(@Param("email") String email);

    @EntityGraph(attributePaths = "roleSet")
    Member findByEmail(String email);

    @Modifying
    @Transactional
    @Query("update Member m set m.mpw =:mpw where m.mid = :mid ")
    void updatePassword(@Param("mpw") String password, @Param("mid") String mid);

    @Query("select m from Member m where m.mid =:mid")
    Member findByMid(@Param("mid") String mid);

    @Query("select m from Member m where m.email =:email")
    Member existsMemberByEmail(String email);

}