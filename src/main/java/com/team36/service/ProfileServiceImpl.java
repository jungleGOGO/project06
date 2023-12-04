package com.team36.service;

import com.team36.domain.Profile;
import com.team36.dto.ProfileDTO;
import com.team36.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ProfileServiceImpl implements ProfileService{
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private ProfileRepository profileRepo;


    @Override
    public int insertProfile(ProfileDTO profileDTO) {
        Profile profile = modelMapper.map(profileDTO, Profile.class);
        return profileRepo.save(profile).getPno();
    }

    @Override
    public int updateProfile(ProfileDTO profileDTO) {
        Profile profile = modelMapper.map(profileDTO, Profile.class);
        return profileRepo.save(profile).getPno();
    }
}
