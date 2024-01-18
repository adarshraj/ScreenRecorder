package in.adarshr.screenrecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class FullScreenCapture extends JFrame {

    private Point pointStart = null;
    private Point pointEnd = null;
    private final ScreenCapturerFullScreen screenCapturerFullScreen = new ScreenCapturerFullScreen();

    public FullScreenCapture() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 1));

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pointStart = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                screenCapturerFullScreen.captureScreen(pointStart, e.getPoint(),"C:/test/screenshot.jpg");
                System.exit(0);
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                pointEnd = e.getPoint();
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (pointStart != null) {
            g.setColor(Color.RED);
            g.drawRect(pointStart.x, pointStart.y,
                    pointEnd.x - pointStart.x, pointEnd.y - pointStart.y);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FullScreenCapture().setVisible(true));
    }
}

class ScreenCapturerFullScreen {

    public void captureScreen(Point startPoint, Point endPoint, String fileName) {
        try {
            Robot robot = new Robot();
            Rectangle area = new Rectangle(startPoint, new Dimension(endPoint.x - startPoint.x, endPoint.y - startPoint.y));
            BufferedImage bufferedImage = robot.createScreenCapture(area);
            ImageIO.write(bufferedImage, "jpg", new File(fileName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}