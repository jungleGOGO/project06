package com.team36.repository;

import com.team36.domain.Community;
import com.team36.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long>, Search{

}
