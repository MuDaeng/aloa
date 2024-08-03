package com.aloa.online.video.mapper;

import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.online.video.dto.LostArkCharacterIdDTO;
import com.aloa.online.video.dto.VideoRegisterResultDTO;
import com.aloa.online.video.event.VideoRegEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VideoSaveMapper {
    VideoSaveMapper INSTANCE = Mappers.getMapper(VideoSaveMapper.class);

    @Mapping(target = "nickName", ignore = true)
    @Mapping(target = "chosung", ignore = true)
    @Mapping(target = "arcana", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    LostArkCharacter toLostArkCharacter(LostArkCharacterIdDTO lostArkCharacterIdDTO);

    VideoRegisterResultDTO toVideoRegisterResultDTO(VideoRegEvent videoRegEvent);
}
