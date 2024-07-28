package com.aloa.online.video.controller;

import com.aloa.common.util.ChosungExtractor;
import com.aloa.common.util.VideoFileUtils;
import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoMapping;
import com.aloa.common.video.handler.*;
import com.aloa.common.video.manager.VideoSaveManager;
import com.aloa.online.video.dto.PathDTO;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube/v1")
public class GoogleApiController {
    private final GoogleApiManager googleApiManager;
    private final VideoSaveManager videoSaveManager;
    private final YoutubeDownloader youtubeDownloader;
    private final VideoFileUtils videoFileUtils;
    private final Tesseract tesseract;
    private final VideoValidator videoValidator;
    private final AloaStarFeignClient aloaStarFeignClient;

    @GetMapping("/youtube")
    public void getUri(@RequestParam String path) {
        var video = googleApiManager.getYoutubeInfo(path);
//        CompletableFuture.runAsync(() -> youtubeDownloader.download(video), downloadExecutor);
    }

    @PostMapping("/reg-video")
    public Video regVideo(@RequestBody PathDTO pathDTO) {
        var youtubeInfo = googleApiManager.getYoutubeInfo(pathDTO.getPath());
        var video = Video.builder()
                .title(youtubeInfo.title())
                .description(youtubeInfo.description())
                .path(youtubeInfo.path())
                .youtubeVideoId(youtubeInfo.youtubeVideoId())
                .chosung(ChosungExtractor.extractChosung(youtubeInfo.title()))
                .clientVersion(null)
                .build();

        video.setCalculationState(CalculationState.WAITING);

        return videoSaveManager.regVideo(video, new VideoMapping());
    }

    @PostMapping("/tnananan")
    public List<RecalculationResult> tnanananan(@RequestBody ReCalculationFiles reCalculationFiles) {
        return aloaStarFeignClient.recalculateForImage(reCalculationFiles);
    }

    @GetMapping("/yanayan")
    public Map<String, List<String>> getYanayan(@RequestParam(required = false) String videoId) {

        long start = System.currentTimeMillis();

        try {
//            var cropInfo = videoFileUtils.cropVideo(videoId);
//            videoFileUtils.extractFrames(cropInfo.zName());
//            videoFileUtils.extractFrames(cropInfo.xName());
//            videoFileUtils.extractFrames(cropInfo.zImage());
//            videoFileUtils.extractFrames(cropInfo.xImage());

            File zfile = new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_z/");
            File xfile = new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_x/");

            String[] zFiles = zfile.list();
            String[] xFiles = xfile.list();
            Loader.load(opencv_imgcodecs.class);
            List<String> zlist = new ArrayList<>(Objects.requireNonNull(zFiles).length);
            List<String> xlist = new ArrayList<>(Objects.requireNonNull(xFiles).length);

            for(String zFile : zFiles) {
                String zFilePath = "src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_z/" + zFile;
                preset(zFilePath);
            }

            for(String xFile : xFiles) {
                String x = "src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_x/" + xFile;
                preset(x);
            }

            for (int i = 0; i < zFiles.length;i ++) {
                String zFile = zFiles[i];

                if ((i * 100 / zFiles.length) % 10 == 0 && ((i * 100 / zFiles.length) != 0)) {
                    System.out.println("Z퍼센트 : " + (i * 100 / zFiles.length));
                }
                try {
                    var file = new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_z/" + zFile);
                    zlist.add(tesseract.doOCR(file).replace("\n", ""));
                } catch (Exception e) {
                    zlist.add("error");
                }
            }

            for (int i = 0; i < xFiles.length; i++) {
                String xFile = xFiles[i];

                if((i * 10000/ xFiles.length ) /100 % 10  == 0 && ((i * 10000/ xFiles.length) / 100 != 0)){
                    System.out.println("X퍼센트 : " + (i * 100 / xFiles.length ));
                }
                try {
                    var file = new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_x/" + xFile);
                    xlist.add(tesseract.doOCR(file).replace("\n", ""));
                } catch (Exception e) {
                    xlist.add("error");
                }
            }

            var result = new HashMap<String, List<String>>();

            result.put("z", zlist.stream().distinct().toList());
            result.put("x", xlist.stream().distinct().toList());

            long end = System.currentTimeMillis();

            System.out.println("걸린시간 : " + (end - start) / 1000 + "s");

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/test")
    public void test(@RequestParam(required = false) Integer i){
            final String crop = "_crop_";
            final String z = "z";
            final String x = "x";
            final String cardImage = "_card_image/";
            final String imageDir = "src/main/resources/ffmpeg/bin/image/";

        Path directory = Paths.get("src/main/resources/ffmpeg/bin/image/flat");

        try{
            Files.createDirectory(directory);
        }catch(IOException ignored){}

        var cardImageIdList = Arrays.asList("5_1Mp9GJK5U");

        List<File> fileList = new ArrayList<>();

        List<String> zlist = new ArrayList<>();
        List<String> xlist = new ArrayList<>();
        var iterator = cardImageIdList.iterator();

        while(iterator.hasNext()){
            var cardImageId = iterator.next();
            File zfile = new File(imageDir + cardImageId + crop + z + cardImage);
            File xfile = new File(imageDir + cardImageId + crop + x + cardImage);
            String[] zFiles = zfile.list();
            for(String zFile : zFiles){
                String path = imageDir + cardImageId + crop + z + cardImage + zFile;
                Path file = Paths.get(path);
                String name = String.format("%07d", i++);
                Path move = Paths.get("src/main/resources/ffmpeg/bin/image/flat/" + name + ".png");

                try {
                    Path newPath = Files.move(file, move);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            String[] xFiles = xfile.list();
            for(String xFile : xFiles){
                String path = imageDir + cardImageId + crop + x + cardImage + xFile;
                Path file = Paths.get(path);
                String name = String.format("%07d", i++);
                Path move = Paths.get("src/main/resources/ffmpeg/bin/image/flat/" + name + ".png");

                try {
                    Path newPath = Files.move(file, move);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }

    private void preset(String filePath){
        Mat zfileMat = opencv_imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE);

        Mat blurMat = new Mat();
        opencv_imgproc.GaussianBlur(zfileMat, blurMat, new Size(1, 1), 0);

        Mat binaryMat = new Mat();
        opencv_imgproc.threshold(blurMat, binaryMat, -1, 255, opencv_imgproc.THRESH_OTSU);

        opencv_imgcodecs.imwrite(filePath, binaryMat);
    }

    private static File createTempImageFile(BufferedImage image, String outputPath) throws IOException {
        File tempFile = new File(outputPath);
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }
}