package com.aloa.common.video.manager;

import com.aloa.common.video.entity.*;
import com.aloa.common.video.repository.*;
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
    private final VideoMappingRepository videoMappingRepository;
    private final VideoCalculationResultRepository videoCalculationResultRepository;
    private final VideoCalculationResultHistRepository videoCalculationResultHistRepository;

    public Video regVideo(@NonNull Video video, VideoMapping videoMapping) {
        var result = videoRepository.save(video);
        this.saveVideoHist(result);

        videoMapping.setVideoId(result.getId());
        videoMappingRepository.save(videoMapping);

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyDownloading(@NonNull Video video) {
        changeCalculationState(video, CalculationState.DOWNLOADING);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyFailedDownload(@NonNull Video video) {
        changeCalculationState(video, CalculationState.FAILED_DOWNLOAD);
    }

    //비디오 계산용
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyCalculating(@NonNull Video video){
        changeCalculationState(video, CalculationState.CALCULATING);
    }

    public void notifyCompleted(@NonNull Video video) {
        changeCalculationState(video, CalculationState.COMPLETED);
    }

    private void changeCalculationState(Video video, @NonNull CalculationState state) {
        video.setCalculationState(state);
        videoRepository.save(video);
        saveVideoHist(video);
    }

    public void saveVideoHist(@NonNull Video video) {
        int nextSeq = videoHistRepository.findFirstByPathOrderByHistSequenceDesc(video.getPath())
                .map(VideoHist::getHistSequence)
                .orElse(0) + 1;
        videoHistRepository.save(new VideoHist(video, nextSeq));
    }

    public void regCalculationResult(@NonNull List<VideoCalculationResult> videoCalculationResultList) {
        videoCalculationResultRepository.saveAll(videoCalculationResultList);
//        saveCalculationResultHist(videoCalculationResultList);
    }

    public void saveCalculationResultHist(@NonNull List<VideoCalculationResult> videoCalculationResultList) {
        videoCalculationResultHistRepository.saveAll(
                videoCalculationResultList.stream()
                        .map(VideoCalculationResultHist::new)
                        .toList()
        );
    }
}
