package com.aloa.common.video.handler;

import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.video.entity.VideoMapping;
import com.aloa.common.video.repository.VideoCalculationResultRepository;
import com.aloa.common.video.repository.VideoMappingRepository;
import com.aloa.common.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoFinder {
    private final VideoRepository videoRepository;
    private final VideoMappingRepository videoMappingRepository;
    private final VideoCalculationResultRepository videoCalculationResultRepository;

    public Optional<Video> findById(Long id) {
        return videoRepository.findById(id);
    }

    public Optional<Video> findByPath(String path) {
        return videoRepository.findByPath(path);
    }

    public Optional<Video> findByYoutubeVideoId(String youtubeVideoId){
        return videoRepository.findByYoutubeVideoId(youtubeVideoId);
    }

    public List<VideoCalculationResult> findCalculationResultByVideoId(Long videoId){
        return videoCalculationResultRepository.findByVideoId(videoId);
    }

    public Optional<VideoMapping> findMappingByVideoId(Long videoId){
        return videoMappingRepository.findByVideoId(videoId);
    }
}
