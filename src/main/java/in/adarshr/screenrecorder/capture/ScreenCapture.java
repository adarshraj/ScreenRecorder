package in.adarshr.screenrecorder.capture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenCapture {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenCapture.class);
    public void captureScreen(Point startPoint, Point endPoint, String fileName, String extension) {
        try {
            Robot robot = new Robot();
            Rectangle area = new Rectangle(startPoint, new Dimension(endPoint.x - startPoint.x, endPoint.y - startPoint.y));
            BufferedImage bufferedImage = robot.createScreenCapture(area);
            ImageIO.write(bufferedImage, extension, new File(fileName));
        } catch (Exception ex) {
            LOGGER.error("Failed to capture", ex);
        }
    }

    public void captureScreen(Rectangle rectangle, String fileName, String extension)  {
        try {
            Robot bot = new Robot();
            BufferedImage screenFullImage = bot.createScreenCapture(rectangle);
            ImageIO.write(screenFullImage, extension, new File(fileName));
        } catch (AWTException | IOException ex) {
            LOGGER.error("Failed to capture", ex);
        }
    }
}
