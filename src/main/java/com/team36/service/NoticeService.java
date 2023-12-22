package com.team36.service;

import com.team36.dto.NoticeDTO;

import java.util.List;

public interface NoticeService {

    public List<NoticeDTO> list();
    public NoticeDTO detail(Integer no);
    public void insert (NoticeDTO noticeDTO);
    public void edit (NoticeDTO noticeDTO);
    public void delete(Integer no);

}
