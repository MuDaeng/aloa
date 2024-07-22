package com.aloa.common.util;

import com.aloa.common.video.handler.OcrResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class VideoFileUtils {
    private final FFprobe ffprobe;
    private final SyncProcessor syncProcessor;
    private static final String ffmPegPath = "src/main/resources/ffmpeg/bin";
    private static final String imageDir = ffmPegPath + "/image/";
    private static final String videoDir = ffmPegPath + "/video/";
    private static final String crop = "_crop_";
    private static final String imagePrefix = "/frame-%05d.png";
    private static final int fps = 3;
    private final Tesseract tesseract;

    public double getDuration(String videoFilePath) throws IOException {
        FFmpegProbeResult probeResult = ffprobe.probe(videoFilePath);

        return probeResult.getFormat().duration;
    }

    private int getFps(String videoFilePath) throws IOException {
        FFmpegProbeResult probeResult = ffprobe.probe(videoFilePath);
        return probeResult.getStreams().getFirst().avg_frame_rate.intValue();
    }

    public void extractFrames(String videoName) {
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
        var cropName = executeFfmpeg(videoName, CropType.NAME);
        var cropImage = executeFfmpeg(videoName, CropType.IMAGE);

        return CropVideoTotal.builder()
                .zName(cropName.z())
                .xName(cropName.x())
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
        Loader.load(opencv_imgcodecs.class);
        for(String dirName : dirList){
            var dir = new File(imageDir + dirName);
            var fileNames = dir.list();
            for(var fileName : Objects.requireNonNull(fileNames)){
                var filePath = imageDir + dirName + fileName;
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

    public OcrResult getOcrImageResult(List<String> dirList){
        var list = new ArrayList<List<String>>();
        for(String dirName : dirList){
            var dir = new File(imageDir + dirName);
            var fileNames = Objects.requireNonNull(dir.list());
            list.add(getOcrImageList(dirName, fileNames));
        }

        return new OcrResult(list.get(0), list.get(1));
    }

    private List<String> getOcrImageList(String dirName, String[] fileNames){
        var ocrList = new ArrayList<String>();
        for(var i = 0; i < fileNames.length; i++){
                var fileName = fileNames[i];

                if ((i * 100 / fileNames.length) % 10 == 0 && ((i * 100 / fileNames.length) != 0)) {
                    System.out.println("Z퍼센트 : " + (i * 100 / fileNames.length));
                }
                try {
                    var file = new File(dirName + fileName);
                    var ocrStr = Optional.of(tesseract.doOCR(file).replace("\n", ""))
                            .filter(str -> !str.isBlank())
                            .orElse(null);
                    ocrList.add(ocrStr);
                } catch (Exception e) {
                    ocrList.add("error");
                }

        }
        return ocrList;
    }

    private String getFormat(String videoName){

        File videoList = new File(videoDir);
        String[] videoFileNames = videoList.list();

        var video = Stream.of(Objects.requireNonNull(videoFileNames)).filter(fileName -> fileName.contains(videoName)).findFirst().orElse(null);

        return "." + Objects.requireNonNull(video).split("\\.")[1];
    }
}
