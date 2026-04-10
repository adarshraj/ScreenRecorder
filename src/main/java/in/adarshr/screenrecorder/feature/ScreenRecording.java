package in.adarshr.screenrecorder.feature;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.EncodingKey;
import static org.monte.media.VideoFormatKeys.FrameRateKey;
import static org.monte.media.VideoFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.VideoFormatKeys.MIME_AVI;
import static org.monte.media.VideoFormatKeys.MediaType;
import static org.monte.media.VideoFormatKeys.MediaTypeKey;
import static org.monte.media.VideoFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.QualityKey;

public class ScreenRecording {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenRecording.class);
    private SpecializedScreenRecorder screenRecorder;
    private String path;

    public boolean startRecording() {
        if (path == null || path.isBlank()) {
            LOGGER.error("startRecording called without a path");
            return false;
        }
        try {
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();
            this.screenRecorder = new SpecializedScreenRecorder(
                    gc,
                    gc.getBounds(),
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                            Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                    null);
            this.screenRecorder.setPath(path);
            this.screenRecorder.start();
            return true;
        } catch (IOException | AWTException e) {
            LOGGER.error("Failed to start screen recording.", e);
            this.screenRecorder = null;
            return false;
        }
    }

    public void stopRecording() {
        if (screenRecorder == null) {
            LOGGER.warn("stopRecording called with no active recorder");
            return;
        }
        try {
            screenRecorder.stop();
        } catch (IOException e) {
            LOGGER.error("Failed to stop screen recording.", e);
        } finally {
            screenRecorder = null;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
