package in.adarshr.screenrecorder.feature;

import in.adarshr.screenrecorder.capture.ScreenCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.Toolkit;

public class FullScreenCapture {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullScreenCapture.class);
    public  void capture(String fileName, String extension) {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            ScreenCapture screenCapture = new ScreenCapture();
            screenCapture.captureScreen(screenRect, fileName, extension);
        } catch (Exception ex) {
            LOGGER.error("Failed to create ScreenRecorder window.", ex);
        }
    }
}
