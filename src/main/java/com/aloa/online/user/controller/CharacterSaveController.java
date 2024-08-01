package com.aloa.online.user.controller;

import com.aloa.online.user.dto.CharacterMappingDTO;
import com.aloa.online.user.service.CharacterSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/character")
@RequiredArgsConstructor
public class CharacterSaveController {
    private final CharacterSaveService characterSaveService;

    @PutMapping("/v1/mapping")
    public void mapCharacter(@RequestBody CharacterMappingDTO character) {
        characterSaveService.mapCharacter(character);
    }
}
