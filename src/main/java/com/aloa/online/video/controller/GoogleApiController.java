package com.aloa.online.video.controller;

import com.aloa.common.util.VideoFileUtils;
import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.manager.GoogleApiManager;
import com.aloa.common.video.manager.VideoSaveManager;
import com.aloa.common.video.youtube.YoutubeDownloader;
import com.aloa.online.video.dto.PathDTO;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube/v1")
public class GoogleApiController {
    private final GoogleApiManager googleApiManager;
    private final VideoSaveManager videoSaveManager;
    private final YoutubeDownloader youtubeDownloader;
    private final Executor downloadExecutor;
    private final VideoFileUtils videoFileUtils;
    private final Tesseract tesseract;

    @GetMapping("youtube")
    public void getUri(@RequestParam String path) {
        var video = googleApiManager.getYoutubeInfo(path);
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> youtubeDownloader.download(video), downloadExecutor);
    }

    @PostMapping("/reg-video")
    public Video regVideo(@RequestBody PathDTO pathDTO) {
        var youtubeInfo = googleApiManager.getYoutubeInfo(pathDTO.getPath());
        youtubeInfo.setCalculationState(CalculationState.WAITING);
        return videoSaveManager.regVideo(youtubeInfo);
    }

    @GetMapping("/yanayan")
    public Map<String, List<String>> getYanayan() {
        var filePath = "src/main/resources/ffmpeg/bin/video/";
        var videoId = "8XRf-pxYPWY";

        long start = System.currentTimeMillis();

        try {
//            var cropInfo = videoFileUtils.cropVideo(videoId);
            videoFileUtils.extractFrames(videoId + "_crop_z");
            videoFileUtils.extractFrames(videoId + "_crop_x");

            File zfile = new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_z/");
            File xfile = new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_x/");

            String[] zFiles = zfile.list();
            String[] xFiles = xfile.list();
            Loader.load(opencv_imgcodecs.class);
            List<String> zlist = new ArrayList<>(zFiles.length);
            List<String> xlist = new ArrayList<>(xFiles.length);

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
                    BufferedImage image = ImageIO.read(new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_z/" + zFile));
//                    var file = setDPI(image, "src/main/resources/ffmpeg/bin/image/d4nMw17JyfU_crop_z/" + zFile, 300);
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
                    BufferedImage image = ImageIO.read(new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_x/" + xFile));
                    //                    var file = setDPI(image, "src/main/resources/ffmpeg/bin/image/d4nMw17JyfU_crop_x/" + xFile, 300);
                    var file = new File("src/main/resources/ffmpeg/bin/image/" + videoId + "_crop_x/" + xFile);
                    xlist.add(tesseract.doOCR(file).replace("\n", ""));
                } catch (Exception e) {
                    xlist.add("error");
                }
            }

            var result = new HashMap<String, List<String>>();

            result.put("z", zlist);
            result.put("x", xlist);

            long end = System.currentTimeMillis();

            System.out.println("걸린시간 : " + (end - start) / 1000 + "s");

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void preset(String filePath){
        Mat zfileMat = opencv_imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE);

        Mat blurMat = new Mat();
        opencv_imgproc.blur(zfileMat, blurMat, new Size(1, 1));

        Mat binaryMat = new Mat();
        opencv_imgproc.threshold(blurMat, binaryMat, 127, 255, opencv_imgproc.THRESH_BINARY | opencv_imgproc.THRESH_OTSU);

        opencv_imgcodecs.imwrite(filePath, binaryMat);
    }

    private static File setDPI(BufferedImage image, String outputPath, int dpi) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDImageXObject pdImage = PDImageXObject.createFromFileByContent(createTempImageFile(image, outputPath), doc);
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth() / dpi * 72, pdImage.getHeight() / dpi * 72);
        contentStream.close();

        doc.save(outputPath);
        doc.close();

        return new File(outputPath);
    }

    private static File createTempImageFile(BufferedImage image, String outputPath) throws IOException {
        File tempFile = new File(outputPath);
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }
}