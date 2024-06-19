package com.aloa.common.video.manager;

import com.aloa.common.video.entity.*;
import com.aloa.common.video.repository.VideoCalculationResultHistRepository;
import com.aloa.common.video.repository.VideoCalculationResultRepository;
import com.aloa.common.video.repository.VideoHistRepository;
import com.aloa.common.video.repository.VideoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class VideoSaveManager {
    private final VideoRepository videoRepository;
    private final VideoHistRepository videoHistRepository;
    private final VideoCalculationResultRepository videoCalculationResultRepository;
    private final VideoCalculationResultHistRepository videoCalculationResultHistRepository;

    public Video regVideo(@NonNull Video video) {
        var result = videoRepository.save(video);
        this.saveVideoHist(result);
        return result;
    }

    //비디오 계산용
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void startVideoCalculation(@NonNull Video video){
        video.setCalculationState(CalculationState.CALCULATING);
    }

    public void saveVideoHist(@NonNull Video video) {
        videoHistRepository.save(new VideoHist(video));
    }

    public List<VideoCalculationResult> regCalculationResult(@NonNull List<VideoCalculationResult> videoCalculationResultList) {
        var result = videoCalculationResultRepository.saveAll(videoCalculationResultList);
        saveCalculationResultHist(videoCalculationResultList);
        return result;
    }

    public void saveCalculationResultHist(@NonNull List<VideoCalculationResult> videoCalculationResultList) {
        videoCalculationResultHistRepository.saveAll(
                videoCalculationResultList.stream()
                        .map(VideoCalculationResultHist::new)
                        .toList()
        );
    }
}
