package com.aloa.online.video.mapper;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface GoogleApiMapper {
    GoogleApiMapper INSTANCE = Mappers.getMapper(GoogleApiMapper.class);

    VideoListResponse toVideoListResponse(Map<String, Object> map);

    VideoSnippet toVideoSnippet(Map<String, Object> snippet);
}
