package com.team36.repository;

import com.team36.domain.Member;
import com.team36.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Search {
    Page<Member> searchPage(Pageable pageable, PageDTO pageDTO);
}
