package com.team36.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.team36.domain.Member;
import com.team36.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class SearchImpl extends QuerydslRepositorySupport implements Search {

    public SearchImpl() {
        super(Member.class);
    }

    @Override
    public Page<Member> searchPage(Pageable pageable, PageDTO pageDTO) {
        QMember member = QMember.member;

        JPAQuery<Member> query = new JPAQuery<>(getEntityManager());

        String[] types = pageDTO.getTypes();
        String keyword = pageDTO.getKeyword();

        if (types != null && keyword != null && !keyword.isEmpty()) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "mname":
                        booleanBuilder.or(member.mname.contains(keyword));
                        break;
                    case "email":
                        booleanBuilder.or(member.email.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

        List<Member> list = query.from(member)
                .where(member.mname.ne("관리자"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults().getResults();

        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }
    }
}
