package com.aloa.online.video.mapper;

import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.online.video.dto.CardCntDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VideoInquiryMapper {
    VideoInquiryMapper INSTANCE = Mappers.getMapper(VideoInquiryMapper.class);

    List<CardCntDTO> toCardCntDTOList(List<VideoCalculationResult> videoCalculationResultList);
}
