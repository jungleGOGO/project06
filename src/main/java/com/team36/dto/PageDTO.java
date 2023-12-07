package com.team36.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<P,D> {

    private static final ModelMapper modelmapper = new ModelMapper();

    private int postpageCount=6;
    private Long postTotal;

    private int pageBegin;
    private int pageEnd;
    private int pageCurrent=1;
    private int pageTotal;
    private int pagingCount=5;

    private String type;
    private String keyword;

    private List<D> pagindDTO;

    public String[] getTypes(){

        if(type==null || type.isEmpty()){
            return null;
        }
        String[] types = type.split(",");


        for(int i=0; i<types.length; i++){
            types[i] = types[i].trim();
        }

        return types;
    }

    public Pageable getPageable(){
        return PageRequest.of(this.pageCurrent-1, this.postpageCount);
    }

    public void build(Page<P> result){
        this.postTotal = result.getTotalElements(); // 이 함수가 long 반환
        this.pageTotal = result.getTotalPages();

        if(this.pageCurrent<=1){
            // 현재 페이지가 0 또는 1인 경우 시작 페이지를 1로 제한 (DivideByZeroException 방지)
            this.pageBegin = 1;
        } else {
            this.pageBegin = ((this.pageCurrent - 1) / this.pagingCount) * this.pagingCount + 1;
        }

        // ex. 시작페이지가 1이면, 마지막페이지는 5
        this.pageEnd = this.pageBegin + this.pagingCount - 1;
        if(this.pageEnd > this.pageTotal) {
            // ex. 만약 전체 페이지가 32고, 시작 페이지가 31이라면, 마지막 페이지는 35가 아니라 32가 되어야 한다.
            this.pageEnd = this.pageTotal;
        }
    }

    public void entity2dto(Page<P> result, Class<D> dto){
        // 기존 엔티티 페이징 결과를 DTO 타입으로 변환
        this.pagindDTO = result.getContent().stream().map(entity -> modelmapper.map(entity, dto)).collect(Collectors.toList());
    }



}
