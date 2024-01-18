package in.adarshr.screenrecorder;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.monte.media.VideoFormatKeys.*;

public class ScreenRecordingTest {
    private ScreenRecorder screenRecorder;

    public static void main(String[] args) throws Exception {
        ScreenRecordingTest test = new ScreenRecordingTest();

        // Start recording
        test.startRecording();

        // Add delay to allow screen recording to start
        Thread.sleep(5000);

        // Stop recording
        test.stopRecording();
    }

    public void startRecording() throws Exception {
        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        File outFile = new File("C:\\test\\ScreenRecordingTest.mov");
        this.screenRecorder = new SpecializedScreenRecorder(
                gc,
                gc.getBounds(),
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                        Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                null);
        this.screenRecorder.start();
    }

    public void stopRecording() throws Exception {
        this.screenRecorder.stop();
    }

    private static class SpecializedScreenRecorder extends ScreenRecorder {

        public SpecializedScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                                         Format screenFormat, Format mouseFormat, Format audioFormat)
                throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat);
        }

        @Override
        protected File createMovieFile(Format fileFormat) {
            return new File("C:\\test\\ScreenRecordingTest.mov");
        }
    }
}