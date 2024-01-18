package in.adarshr.screenrecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class UserCapture extends JPanel {

    private Point pointStart = null;
    private Point pointEnd = null;
    private final ScreenCapturer screenCapturer = new ScreenCapturer();

    public UserCapture() {
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pointStart = new Point(e.getX(), e.getY());
            }

            public void mouseReleased(MouseEvent e) {
                screenCapturer.captureScreen(pointStart, e.getPoint(), "C:/path-to-save/screenshot.jpg");
                pointStart = null;
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                pointEnd = new Point(e.getX(), e.getY());
                repaint();
            }

            public void mouseDragged(MouseEvent e) {
                pointEnd = new Point(e.getX(), e.getY());
                repaint();
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (pointStart != null) {
            g.setColor(Color.RED);
            g.drawRect(pointStart.x, pointStart.y,
                    pointEnd.x - pointStart.x, pointEnd.y - pointStart.y);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        UserCapture userCapture = new UserCapture();
        frame.add(userCapture);
        frame.setVisible(true);
    }
}

class ScreenCapturer {

    public void captureScreen(Point startPoint, Point endPoint, String fileName) {
        try {
            Robot robot = new Robot();
            Rectangle area = new Rectangle(startPoint, new Dimension(endPoint.x - startPoint.x, endPoint.y - startPoint.y));
            BufferedImage bufferedImage = robot.createScreenCapture(area);
            ImageIO.write(bufferedImage, "jpg", new File(fileName));
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}