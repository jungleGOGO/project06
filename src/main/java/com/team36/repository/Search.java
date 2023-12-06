package com.team36.repository;

import com.team36.domain.Community;
import com.team36.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Search {
    Page<Community> searchPage(Pageable pageable, PageDTO pageDTO);
}
