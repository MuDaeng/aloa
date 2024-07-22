package com.aloa.common.video.handler;

import com.google.api.client.util.DateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PACKAGE)
public record YoutubeInfo(String title,
                          String description,
                          String path,
                          String youtubeVideoId,
                          DateTime publishedAt,
                          String channelId) {
}
