package com.team36.service;

import com.team36.domain.Member;
import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.ProfileDTO;
import com.team36.repository.ProfileRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@Log4j2
public class ProfileServiceImpl implements ProfileService{
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private ProfileRepository profileRepo;


    @Override
    public int insertProfile(ProfileDTO profileDTO) {
        Profile pf = modelMapper.map(profileDTO, Profile.class);
        profileRepo.save(pf);
        return profileRepo.save(pf).getPno();
    }

    @Override
    public void updateProfile(ProfileDTO profileDTO) {
        Optional<Profile> result = profileRepo.findById(profileDTO.getPno());
        Profile pf = result.orElseThrow();
        pf.change(profileDTO.getIntro(), profileDTO.getGitLink1(), profileDTO.getGitLink2());
        profileRepo.save(pf);
    }

    @Override
    public Profile existsProfileByMember(Member member) {
        return profileRepo.existsProfileByMember(member);
    }

}
