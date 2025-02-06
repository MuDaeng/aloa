package com.aloa.online.card.mapper;

import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.card.dto.CardCnt;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserCardInquiryMapper {
    UserCardInquiryMapper INSTANCE = Mappers.getMapper(UserCardInquiryMapper.class);

    List<CardCnt> toCardCntDTOList(List<VideoCalculationResult> videoCalculationResultList);
}
