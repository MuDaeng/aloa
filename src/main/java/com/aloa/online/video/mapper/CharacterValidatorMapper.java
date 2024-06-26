package com.aloa.online.video.mapper;

import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.common.user.entitiy.primarykey.LostArkCharacterPK;
import com.aloa.online.video.dto.LostArkCharacterIdDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CharacterValidatorMapper {
    CharacterValidatorMapper INSTANCE = Mappers.getMapper(CharacterValidatorMapper.class);

    @Mapping(target = "nickName", ignore = true)
    @Mapping(target = "chosung", ignore = true)
    @Mapping(target = "arcana", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "expedition", ignore = true)
    @Mapping(target = "videoList", ignore = true)
    LostArkCharacter toLostArkCharacter(LostArkCharacterIdDTO lostArkCharacterIdDTO);
}
