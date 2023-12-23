package com.team36.service;

import com.team36.domain.Member;
import com.team36.domain.Notice;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.NoticeDTO;
import com.team36.dto.PageDTO;
import com.team36.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServcieImpl implements NoticeService{

    private final ModelMapper modelMapper;
    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDTO> list() {
        List<Notice> lst = noticeRepository.findAll();
        List<NoticeDTO> list = lst.stream().map(notice ->modelMapper.map(notice,NoticeDTO.class)).collect(Collectors.toList());

        return list;
    }

    @Override
    public NoticeDTO detail(Integer no) {
        Optional<Notice> detail = noticeRepository.findById(no);
        NoticeDTO dto = modelMapper.map(detail, NoticeDTO.class);

        return dto;
    }

    @Override
    public void insert(NoticeDTO noticeDTO) {
        Notice notice = modelMapper.map(noticeDTO, Notice.class);
        noticeRepository.save(notice);
    }

    @Override
    public void edit(NoticeDTO noticeDTO) {
        Optional<Notice> notice = noticeRepository.findById(noticeDTO.getNo());
        Notice result = notice.orElseThrow();
        result.change(noticeDTO.getTitle(), noticeDTO.getContent());
        noticeRepository.save(result);
    }

    @Override
    public void delete(Integer no) {
        noticeRepository.deleteById(no);
    }

    @Override
    public PageDTO<Notice, NoticeDTO> noticeList(PageDTO<Notice, NoticeDTO> pageDTO) {
        Pageable pageable = pageDTO.getPageable();
        Page<Notice> result = noticeRepository.noticeSearchPage(pageable,pageDTO);
        pageDTO.build(result);
        pageDTO.entity2dto(result, NoticeDTO.class);
        return pageDTO;
    }

}
