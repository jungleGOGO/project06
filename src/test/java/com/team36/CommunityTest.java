package com.team36;

import com.team36.dto.CommunityDTO;
import com.team36.repository.CommunityRepository;
import com.team36.service.CommunityService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Principal;

@SpringBootTest
@Log4j2
public class CommunityTest {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityService communityService;

    @Test
    public void testRegister() {

        CommunityDTO community = CommunityDTO.builder()
                .title("제목")
                .content("내용")
                .author("글쓴이")
                .build();

        communityService.communityAdd(community);
    }

}
