package com.team36.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.team36.domain.Community;
import com.team36.domain.QCommunity;
import com.team36.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class SearchImpl extends QuerydslRepositorySupport implements Search {

    public SearchImpl() {
        super(Community.class);
    }

    @Override
    public Page<Community> searchPage(Pageable pageable, PageDTO pageDTO) {
        QCommunity community = QCommunity.community; // Correct entity class

        JPAQuery<Community> query = new JPAQuery<>(getEntityManager());

        String[] types = pageDTO.getTypes();
        String keyword = pageDTO.getKeyword();

        if (types != null && keyword != null && !keyword.isEmpty()) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "title":
                        booleanBuilder.or(community.title.contains(keyword));
                        break;
                    case "content":
                        booleanBuilder.or(community.content.contains(keyword));
                        break;
                    case "author":
                        booleanBuilder.or(community.author.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

        List<Community> list = query.from(community)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults().getResults();

        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }
}
