package com.aloa.online.card.mapper;

import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.online.card.dto.CardCntDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CardInquiryMapper {
    CardInquiryMapper INSTANCE = Mappers.getMapper(CardInquiryMapper.class);

    List<CardCntDTO> toCardCntDTOList(List<VideoCalculationResult> videoCalculationResultList);
}
