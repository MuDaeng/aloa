package com.aloa.common.video.handler;

import com.aloa.common.video.entity.Video;
import com.aloa.common.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoFinder {
    private final VideoRepository videoRepository;

    public Optional<Video> findById(Long id) {
        return videoRepository.findById(id);
    }

    public Optional<Video> findByPath(String path) {
        return videoRepository.findByPath(path);
    }

    public Optional<Video> findByYoutubeVideoId(String youtubeVideoId){
        return videoRepository.findByYoutubeVideoId(youtubeVideoId);
    }
}
