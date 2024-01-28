package in.adarshr.screenrecorder;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

import static org.monte.media.VideoFormatKeys.*;

public class ScreenRecording {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenRecording.class);
    private SpecializedScreenRecorder screenRecorder;
    private String path;


    public static void main(String[] args) throws Exception {
        ScreenRecording test = new ScreenRecording();

        // Start recording
        test.startRecording();

        // Add delay to allow screen recording to start
        Thread.sleep(3000);

        // Stop recording
        test.stopRecording();

        System.exit(0);
    }

    public void startRecording() {
        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        try {
            this.screenRecorder = new SpecializedScreenRecorder(
                    gc,
                    gc.getBounds(),
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                            Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                    null);
            this.screenRecorder.setPath(getPath());
            this.screenRecorder.start();
        } catch (IOException | AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopRecording(){
        try {
            this.screenRecorder.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}