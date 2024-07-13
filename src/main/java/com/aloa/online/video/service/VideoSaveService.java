package com.aloa.online.video.service;

import com.aloa.common.card.entity.Engrave;
import com.aloa.common.user.validator.CharacterValidator;
import com.aloa.common.util.SignedInUser;
import com.aloa.common.util.SignedInUserUtil;
import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.manager.GoogleApiManager;
import com.aloa.common.video.manager.VideoSaveManager;
import com.aloa.common.video.validator.VideoValidator;
import com.aloa.common.video.youtube.YoutubeDownloader;
import com.aloa.online.video.calculator.VideoCalculator;
import com.aloa.online.video.dto.LostArkCharacterIdDTO;
import com.aloa.online.video.dto.VideoRegisterDTO;
import com.aloa.online.video.mapper.CharacterValidatorMapper;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoSaveService {
    private final VideoSaveManager videoSaveManager;
    private final VideoValidator videoValidator;
    private final GoogleApiManager googleApiManager;
    private final CharacterValidator characterValidator;
    private final YoutubeDownloader youtubeDownloader;

    private final Executor downloadExecutor;

    public void regist(@Valid VideoRegisterDTO videoRegisterDTO){
        final String path = videoRegisterDTO.getPath();
        final String engrave = videoRegisterDTO.getEngrave();

        //비디오가 이미 등록되어 있으면 비디오의 계산상태코드로 메시지 리턴
        if(videoValidator.isDuplicated(path)){
            throw new IllegalArgumentException("Duplicated path");
        }

        //
        var youtubeVideo = googleApiManager.getYoutubeInfo(path);

        if(youtubeVideo == null) throw new IllegalArgumentException("잘못된 경로입니다.");

        SignedInUser signedInUser = SignedInUserUtil.getSignedInUser();

        if(!signedInUser.isVideoOfUser(youtubeVideo)) throw new IllegalArgumentException("다른사람의 영상입니다.");

        if(!(videoRegisterDTO.getCharacterId() == null)) mapCharacter(youtubeVideo, videoRegisterDTO.getCharacterId());

        Map<String, Engrave> engraveMap = new HashMap<>();
        engraveMap.put("EMPRESS", Engrave.EMPRESS);
        engraveMap.put("EMPEROR", Engrave.EMPEROR);
        youtubeVideo.setEngrave(engraveMap.get(engrave));

        youtubeVideo.setCalculationState(CalculationState.WAITING);

        videoSaveManager.regVideo(youtubeVideo);

        requestDownload(youtubeVideo);
    }


    public void mapCharacter(@NonNull Video video, @NonNull @Valid LostArkCharacterIdDTO lostArkCharacterIdDTO){
        var character = CharacterValidatorMapper.INSTANCE.toLostArkCharacter(lostArkCharacterIdDTO);
        character = characterValidator.findCharacter(character);

        if(character == null || !character.isArcana()){
            throw new IllegalArgumentException("잘못된 캐릭터정보를 입력하였습니다.");
        }

        video.mapCharacter(character);
    }

    private void requestDownload(Video video){
        CompletableFuture.runAsync(() -> youtubeDownloader.download(video), downloadExecutor);
    }
}
