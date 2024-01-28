package in.adarshr.screenrecorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class FullScreenCapture {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullScreenCapture.class);
    public  void capture(String fileName, String extension) {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            new ScreenCapture().captureScreen(screenRect, fileName, extension);
        } catch (Exception ex) {
            LOGGER.error("Failed to create ScreenRecorder window.", ex);
        }
    }
}
