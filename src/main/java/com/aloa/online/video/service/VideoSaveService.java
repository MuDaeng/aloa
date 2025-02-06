package com.aloa.online.video.service;

import com.aloa.common.card.entity.Engrave;
import com.aloa.common.card.entity.SideNode;
import com.aloa.common.client.entity.ClientVersion;
import com.aloa.common.client.manager.ClientVersionManager;
import com.aloa.common.user.validator.CharacterValidator;
import com.aloa.common.util.ChosungExtractor;
import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoMapping;
import com.aloa.common.video.handler.GoogleApiManager;
import com.aloa.common.video.handler.VideoValidator;
import com.aloa.common.video.handler.YoutubeInfo;
import com.aloa.common.video.manager.VideoSaveManager;
import com.aloa.common.video.repository.VideoMappingRepository;
import com.aloa.online.video.dto.VideoMappingDTO;
import com.aloa.online.video.dto.LostArkCharacterIdDTO;
import com.aloa.online.video.dto.VideoRegisterDTO;
import com.aloa.online.video.dto.VideoRegisterResultDTO;
import com.aloa.online.video.event.VideoRegEvent;
import com.aloa.online.video.mapper.VideoSaveMapper;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoSaveService {
    private final VideoSaveManager videoSaveManager;
    private final VideoValidator videoValidator;
    private final GoogleApiManager googleApiManager;
    private final CharacterValidator characterValidator;
    private final ClientVersionManager clientVersionManager;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VideoMappingRepository videoMappingRepository;

    public VideoRegisterResultDTO registerVideo(@Valid VideoRegisterDTO videoRegisterDTO){
        final String path = videoRegisterDTO.getPath();
        final String engrave = videoRegisterDTO.getEngrave();

        //비디오가 이미 등록되어 있으면 비디오의 계산상태코드로 메시지 리턴
        if(videoValidator.isDuplicated(path)){
            throw new IllegalArgumentException("Duplicated path");
        }

        //
        var youtubeVideo = googleApiManager.getYoutubeInfo(path);

        if(youtubeVideo == null) throw new IllegalArgumentException("잘못된 경로입니다.");

        var video = toVideo(youtubeVideo);

        var videoMapping = VideoMapping.createVideoMapping();

        if(!videoValidator.isVideoOfUser(youtubeVideo.channelId())) throw new IllegalArgumentException("다른사람의 영상입니다.");

        Optional.ofNullable(videoRegisterDTO.getCharacterId())
                .ifPresent(characterId -> mapCharacter(videoMapping, characterId));

        video.setEngrave(Engrave.getEngrave(engrave));

        video.setCalculationState(CalculationState.WAITING);
        video.setSideNode(SideNode.KNIGHT.name().equals(videoRegisterDTO.getSideNode()) ? SideNode.KNIGHT : SideNode.NONE);

        videoSaveManager.regVideo(video, videoMapping);

        var videoRegEvent = new VideoRegEvent(video.getId());
        applicationEventPublisher.publishEvent(videoRegEvent);

        return VideoSaveMapper.INSTANCE.toVideoRegisterResultDTO(videoRegEvent);
    }

    public void changeCharacter(@NonNull VideoMappingDTO videoMappingDTO) {
        if(!videoValidator.isVideoOfUser(videoMappingDTO.getVideoId())) throw new IllegalArgumentException("비디오 정보가 정확하지 않습니다.");

        var videoMapping = videoMappingRepository.findByVideoId(videoMappingDTO.getVideoId())
                .orElseThrow(() -> new IllegalArgumentException("등록된 비디오가 없습니다."));
        mapCharacter(videoMapping, videoMappingDTO.getLostArkCharacterIdDTO());
    }

    private void mapCharacter(VideoMapping videoMapping, @NonNull @Valid LostArkCharacterIdDTO lostArkCharacterIdDTO){
        var character = VideoSaveMapper.INSTANCE.toLostArkCharacter(lostArkCharacterIdDTO);
        character = characterValidator.findCharacter(character);

        if(character == null || !character.isArcana()){
            throw new IllegalArgumentException("잘못된 캐릭터정보를 입력하였습니다.");
        }

        videoMapping.mapCharacter(character);
    }

    private Video toVideo(YoutubeInfo youtubeInfo){
        return Video.builder()
                .title(youtubeInfo.title())
                .description(youtubeInfo.description())
                .path(youtubeInfo.path())
                .youtubeVideoId(youtubeInfo.youtubeVideoId())
                .chosung(ChosungExtractor.extractChosung(youtubeInfo.title()))
                .clientVersion(
                        Optional.ofNullable(
                                        clientVersionManager.getClientVersion(youtubeInfo.publishedAt())
                                )
                                .map(ClientVersion::getVersion)
                                .orElse(null))
                .build();
    }
}
