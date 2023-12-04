package com.team36.service;

import com.team36.domain.Community;
import com.team36.dto.CommunityDTO;
import com.team36.dto.PageDTO;
import com.team36.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService{

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private CommunityRepository communityRepository;


    public Long communityAdd(CommunityDTO communityDTO) {
        Community community =  modelMapper.map(communityDTO, Community.class);
        return communityRepository.save(community).getCno();
    }

    @Override
    public PageDTO<Community, CommunityDTO> communityList(PageDTO<Community, CommunityDTO> pageDTO) {
        Pageable pageable = pageDTO.getPageable();
        Page<Community> result = communityRepository.searchPage(pageable, pageDTO);
        pageDTO.build(result);
        pageDTO.entity2dto(result, CommunityDTO.class);
        return pageDTO;
    }

}
