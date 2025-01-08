package com.aloa.common.util;

import com.aloa.common.video.feignclient.AloaStarFeignClient;
import com.aloa.common.video.feignclient.vo.RecalculationResult;
import com.aloa.common.video.handler.OcrResult;
import com.aloa.common.video.handler.ReCalculationFiles;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.sourceforge.tess4j.Tesseract;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoFileUtils {
    private final FFprobe ffprobe;
    private final Tesseract tesseract;
    private final SyncProcessor syncProcessor;
    private final AloaStarFeignClient aloaStarFeignClient;

    private static final String ffmPegPath = "src/main/resources/ffmpeg/bin";
    private static final String imageDir = ffmPegPath + "/image/";
    private static final String videoDir = ffmPegPath + "/video/";
    private static final String crop = "_crop_";
    private static final String imagePrefix = "/frame-%05d.png";
    private static final int fps = 4;

    public void extractFrames(String videoName) {
        log.debug("extract frames from {}", videoName);
        Path directory = Paths.get(imageDir + videoName);
        try{
            Files.createDirectory(directory);
        } catch (FileAlreadyExistsException ignored){}
        catch(IOException ie){
            throw new IllegalArgumentException(ie);
        }

        var format = getFormat(videoName);

        var inputFilePath = videoDir + videoName + format;
        var outputFilePath = imageDir + videoName + imagePrefix;
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", inputFilePath, "-vf", "fps=" + fps, outputFilePath);
        try{
            syncProcessor.startProcess(pb, "ffmpeg");
        }catch(IOException | InterruptedException ie){
            throw new IllegalArgumentException(ie);
        }
    }

    public CropVideoTotal cropVideo(String videoName) throws IOException, InterruptedException {
        log.debug("crop video from {}", videoName);

        var cropType = CropType.FHD;

        var videoSize = getVideoSize(videoName);

        if(!(videoSize.width() == 1920 && videoSize.height() == 1080)) cropType = CropType.UHD;

        var cropImage = executeFfmpeg(videoName, cropType);

        return CropVideoTotal.builder()
                .zImage(cropImage.z())
                .xImage(cropImage.x())
                .build();
    }

    private CropVideoNameInfo executeFfmpeg(String videoName, @NonNull CropType cropType) throws IOException, InterruptedException {
        var format = getFormat(videoName);

        var inputFilePath = videoDir + videoName + format;

        FFmpegProbeResult probeResult = ffprobe.probe(inputFilePath);
        FFmpegStream ffmpegStream = probeResult.getStreams().getFirst();

        int width = ffmpegStream.width;
        int height = ffmpegStream.height;

        int widthMultiple = cropType.getWidthMultiple();
        int heightMultiple = cropType.getHeightMultiple();

        int widthFixed = width /widthMultiple / 12;

        int heightFixed = height /heightMultiple / 10;

        int y = (((height / heightMultiple) * (heightMultiple - 1)) * cropType.getStartYRatio()) / 1000;

        int zx = width / 2 - (width / widthMultiple / 2);   // Z좌표 crop 시작점
        int xx = (width / 2) + widthFixed; // X좌표 crop 시작점

        width = (width / widthMultiple / 2) - widthFixed;

        height = (height / heightMultiple / 2) - heightFixed;

        var scale = cropType.getScale();

        var zName = videoName + crop + "z" + cropType.getAnnexationName();
        var xName = videoName + crop + "x" + cropType.getAnnexationName();

        var outputImageFilePathZ = videoDir + zName + format;
        var cropZ = width + ":" + height + ":" + zx + ":" + y;

        executeFfmpeg(inputFilePath, outputImageFilePathZ, cropZ, scale);

        var outputImageFilePathX = videoDir + xName + format;
        var cropX = width + ":" + height + ":" + xx + ":" + y;

        executeFfmpeg(inputFilePath, outputImageFilePathX, cropX, scale);

        return CropVideoNameInfo.builder()
                .z(zName)
                .x(xName)
                .build();
    }

    private void executeFfmpeg(String inputFilePath, String outputFilePath, String crop, String scale) throws IOException, InterruptedException {
        if(scale != null && !scale.isBlank()){
            crop += scale;
        }
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", inputFilePath, "-vf", "crop=" + crop, outputFilePath);
        syncProcessor.startProcess(pb, "ffmpeg");
    }

    public void preprocessOcrImage(List<String> dirList){
        log.debug("preprocess ocr image {}", dirList);
        Loader.load(opencv_imgcodecs.class);
        for(String dirName : dirList){
            var dir = new File(imageDir + dirName);
            var fileNames = dir.list();
            for(var fileName : Objects.requireNonNull(fileNames)){
                var filePath = imageDir + dirName + "/" + fileName;
                preprocessOcrImage(filePath);
            }
        }
    }

    private void preprocessOcrImage(String filePath){
        var zfileMat = opencv_imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE);

        var blurMat = new Mat();
        opencv_imgproc.GaussianBlur(zfileMat, blurMat, new Size(1, 1), 0);

        var binaryMat = new Mat();
        opencv_imgproc.threshold(blurMat, binaryMat, -1, 255, opencv_imgproc.THRESH_OTSU);

        opencv_imgcodecs.imwrite(filePath, binaryMat);
    }

    private VideoSize getVideoSize(String videoName) throws IOException {
        var format = getFormat(videoName);

        var inputFilePath = videoDir + videoName + format;

        FFmpegProbeResult videoMetaData = ffprobe.probe(inputFilePath);

        var videoSize = videoMetaData.getStreams().getFirst();

        return new VideoSize(videoSize.width, videoSize.height);
    }

    public OcrResult getOcrImageResult(List<String> cardImageList){
        log.debug("getOcrImageResult {}", cardImageList);
        var list = new ArrayList<List<String>>();
        for (String imageName : cardImageList) {
            var dir = new File(imageDir + imageName);
            var fileNames = Objects.requireNonNull(dir.list());
            list.add(getOcrImageList(fileNames, imageName));
        }

        return new OcrResult(list.get(0), list.get(1));
    }

    private List<String> getOcrImageList(String[] fileNames, String cardImage){
        var absolutePath = Optional.of(new File(imageDir + cardImage))
                .map(File::getAbsolutePath)
                .orElseThrow(() -> new IllegalArgumentException("directory is not found"));

        int i = 0;

        var recalculationResults = new ArrayList<RecalculationResult>();

        while(i < fileNames.length){
            var sublist = List.of(fileNames);
            if((i + 100) > fileNames.length){
                sublist = sublist.subList(i, fileNames.length);
            }else{
                sublist = sublist.subList(i, i + 100);
            }
            var recalculationFiles = new ReCalculationFiles(absolutePath, sublist);
            var subResult = aloaStarFeignClient.recalculateForImage(recalculationFiles);
            System.out.println(subResult);
            recalculationResults.addAll(subResult);
            i += 100;
        }

        return recalculationResults.stream()
                .sorted(Comparator.comparing(RecalculationResult::fileName))
                .map(RecalculationResult::cardName)
                .toList();
    }

    private String getFormat(String videoName){

        File videoList = new File(videoDir);
        String[] videoFileNames = videoList.list();

        var video = Stream.of(Objects.requireNonNull(videoFileNames)).filter(fileName -> fileName.contains(videoName)).findFirst().orElse(null);

        return "." + Objects.requireNonNull(video).split("\\.")[1];
    }
}
