package com.aloa.online.video.mapper;

import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.common.user.entitiy.primarykey.LostArkCharacterPK;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CharacterValidatorMapper {
    CharacterValidatorMapper INSTANCE = Mappers.getMapper(CharacterValidatorMapper.class);

    LostArkCharacter toLostArkCharacter(LostArkCharacterPK lostArkCharacterPK);
}
