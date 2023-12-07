package com.team36.service;

import com.team36.domain.Member;
import com.team36.domain.Profile;
import com.team36.dto.ProfileDTO;
import com.team36.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class ProfileServiceImpl implements ProfileService{
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private ProfileRepository profileRepo;


    @Override
    public int insertProfile(Profile profile) {
        Profile pf = modelMapper.map(profile, Profile.class);
        return profileRepo.save(pf).getPno();
    }

    @Override
    public void updateProfile(Profile profile) {
        Optional<Profile> result = profileRepo.findById((long) profile.getPno());
        Profile pf = result.orElseThrow();
        pf.change(profile.getIntro(), profile.getGitLink1(), profile.getGitLink2());
        profileRepo.save(pf);
    }

    @Override
    public Profile existsProfileByMember(Member member) {
        return profileRepo.existsProfileByMember(member);
    }
}
