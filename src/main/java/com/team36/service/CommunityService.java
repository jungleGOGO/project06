package com.team36.service;

import com.team36.domain.Community;
import com.team36.dto.CommunityDTO;
import com.team36.dto.PageDTO;

public interface CommunityService {
    PageDTO<Community, CommunityDTO> communityList(PageDTO<Community, CommunityDTO> pageDTO);
    Long communityAdd(CommunityDTO communityDTO);

}
