package com.aloa.common.statistics.entity;

import com.aloa.common.video.entity.Video;
import jakarta.validation.Valid;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReservedVideoList {
    private final List<CalculationReservedVideo> calculationReservedVideoList;

    {
        calculationReservedVideoList = new ArrayList<>();
    }

    public void addVideo(@NonNull @Valid Video video) {
        calculationReservedVideoList.add(new CalculationReservedVideo(video));
    }

    public boolean addVideoList(@NonNull @Valid List<Video> videoList) {
        videoList = videoList.stream()
                .filter(
                        video -> calculationReservedVideoList.stream()
                                .noneMatch(
                                        reserved -> Objects.equals(reserved.getVideoId(), video.getId())
                                )
                ).toList();
        return calculationReservedVideoList.addAll(videoList.stream().map(CalculationReservedVideo::new).toList());
    }

    public boolean removeVideo(@NonNull @Valid Video video) {
        return calculationReservedVideoList.removeIf(reserved -> Objects.equals(reserved.getVideoId(), video.getId()));
    }

    public boolean removeVideoList(@NonNull @Valid List<Video> videoList) {
        return calculationReservedVideoList.removeIf(
                reserved -> videoList.stream()
                        .anyMatch(
                                video -> Objects.equals(reserved.getVideoId(), video.getId())
                )
        );
    }

    public List<CalculationReservedVideo> getReservedVideoList() {
        return List.copyOf(calculationReservedVideoList);
    }


}
