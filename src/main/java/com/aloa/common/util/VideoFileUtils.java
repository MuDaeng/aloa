package com.aloa.common.util;

import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class VideoFileUtils {
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final SyncProcessor syncProcessor;
    private static final String ffmPegPath = "src/main/resources/ffmpeg/bin";
    private static final String imageDir = ffmPegPath + "/image/";
    private static final String videoDir = ffmPegPath + "/video/";
    private static final String crop = "_crop_";
    private static final String imagePrefix = "/frame-%05d.png";
    private static final int fps = 3;

    public double getDuration(String videoFilePath) throws IOException {
        FFmpegProbeResult probeResult = ffprobe.probe(videoFilePath);

        return probeResult.getFormat().duration;
    }

    private int getFps(String videoFilePath) throws IOException {
        FFmpegProbeResult probeResult = ffprobe.probe(videoFilePath);
        return probeResult.getStreams().getFirst().avg_frame_rate.intValue();
    }

    public void extractFrames(String videoName) throws IOException, InterruptedException {
        Path directory = Paths.get(imageDir + videoName);
        try{
            Files.createDirectory(directory);
        }catch(FileAlreadyExistsException ignored){}

//        FFmpegBuilder builder = new FFmpegBuilder()
//                .setInput(videoDir + videoName + videoFormat)     // 비디오 파일 경로
//                .addOutput(imageDir + videoName + filePrefix)   // 출력 이미지 파일 패턴
//                .setFrames(3)
//                .done();
//        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
//        executor.createJob(builder, Progress.)
        var format = getFormat(videoName);

        var inputFilePath = videoDir + videoName + format;
        var outputFilePath = imageDir + videoName + imagePrefix;
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", inputFilePath, "-vf", "fps=" + fps, outputFilePath);
        syncProcessor.startProcess(pb, "ffmpeg");
    }

    public CropVideoNameInfo cropVideo(String videoName) throws IOException, InterruptedException {
        var format = getFormat(videoName);

        var inputFilePath = videoDir + videoName + format;

        FFmpegProbeResult probeResult = ffprobe.probe(inputFilePath);
        FFmpegStream ffmpegStream = probeResult.getStreams().getFirst();

        int width = ffmpegStream.width;
        int height = ffmpegStream.height;

        int widthMultiple = 15;
        int heightMultiple = 15;

        int widthFixed = width /widthMultiple / 12;

        int heightFixed = height /heightMultiple / 10;

        int y = height / heightMultiple * (heightMultiple - 1);

        int zx = width / 2 - (width / widthMultiple / 2);   // Z좌표 crop 시작점
        int xx = (width / 2) + widthFixed; // X좌표 crop 시작점

        width = (width / widthMultiple / 2) - widthFixed;

        height = (height / heightMultiple / 2) - heightFixed;


        var outputFilePathZ = videoDir + videoName + crop + "z" + format;
        var cropZ = width + ":" + height + ":" + zx + ":" + y;

        cropVideo(inputFilePath, outputFilePathZ, cropZ);

        var outputFilePathX = videoDir + videoName + crop + "x" + format;
        var cropX = width + ":" + height + ":" + xx + ":" + y;

        cropVideo(inputFilePath, outputFilePathX, cropX);

        return new CropVideoNameInfo(videoName + crop + "z", videoName + crop + "x");
    }

    private void cropVideo(String inputFilePath, String outputFilePath, String crop) throws IOException, InterruptedException {
        String scale = ",scale=384:216";
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", inputFilePath, "-vf", "crop=" + crop + scale, outputFilePath);
        syncProcessor.startProcess(pb, "ffmpeg");
    }


    public List<String> getImageList(String videoName) {
        File dir = new File(imageDir + videoName + "/");

        var imageList = dir.list();

        if(imageList == null) return Collections.emptyList();

        return List.of(Objects.requireNonNull(dir.list()));
    }

    private String getFormat(String videoName){
        File videoList = new File("src/main/resources/ffmpeg/bin/video/");
        String[] videoFileNames = videoList.list();

        var video = Stream.of(Objects.requireNonNull(videoFileNames)).filter(fileName -> fileName.contains(videoName)).findFirst().orElse(null);

        return "." + Objects.requireNonNull(video).split("\\.")[1];
    }
}
