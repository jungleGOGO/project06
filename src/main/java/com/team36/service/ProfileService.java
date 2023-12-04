package com.team36.service;

import com.team36.domain.Profile;
import com.team36.dto.ProfileDTO;

public interface ProfileService {
    int insertProfile(ProfileDTO profileDTO);

    int updateProfile(ProfileDTO profileDTO);
}
