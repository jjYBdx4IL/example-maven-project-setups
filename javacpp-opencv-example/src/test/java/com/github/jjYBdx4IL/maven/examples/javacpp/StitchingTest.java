package com.github.jjYBdx4IL.maven.examples.javacpp;

import static com.github.jjYBdx4IL.maven.examples.javacpp.Stitching.try_use_gpu;
import java.io.File;
import java.net.URISyntaxException;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import org.bytedeco.javacpp.opencv_stitching.Stitcher;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class StitchingTest {

    private static final Logger log = LoggerFactory.getLogger(StitchingTest.class);
    
    @Ignore
    @Test
    public void testStitching() throws URISyntaxException {
        //System.getProperties().list(System.out);
        assertEquals("slf4j", System.getProperty("org.bytedeco.javacpp.logger"));
        
        File imgInputFile = new File(
                StitchingTest.class.getResource("/org/openimaj/image/contour/aestheticode/aestheticode.jpg").toURI());

        MatVector imgs = new MatVector();
        for (int i = 0; i < 3; i++) {
            Mat img = imread(imgInputFile.getAbsolutePath());
            assertFalse(img.empty());
            imgs.resize(imgs.size() + 1);
            imgs.put(imgs.size() - 1, img);
        }
        
        Mat pano = new Mat();
        Stitcher stitcher = Stitcher.createDefault(try_use_gpu);
        int status = stitcher.stitch(imgs, pano);

        assertEquals(Stitcher.OK, status);

        String result_name = new File(System.getProperty("basedir"), "stitched.jpg").getAbsolutePath();
        imwrite(result_name, pano);
    }

}
