package in.adarshr.screenrecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserDefinedCapture extends JFrame {

    private Point pointStart = null;
    private Point pointEnd = null;
    private Boolean isDisposed = false;
    private Rectangle rectangle;
    private String filePath;
    private String fileExtension;
    public UserDefinedCapture() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 1));

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pointStart = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                setRectangle(pointStart, e.getPoint());
                capture(getFilePath(), getFileExtension());
                setDisposed(true);
                dispose();
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                pointEnd = e.getPoint();
                repaint();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                for (Window window : Window.getWindows()) {
                    if ("ScreenRecorder".equals(window.getName())) {
                        window.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public Boolean isDisposed(){
        return isDisposed;
    }

    public void setDisposed(Boolean disposed){
        isDisposed = disposed;
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
        SwingUtilities.invokeLater(() -> new UserDefinedCapture().setVisible(true));
    }

    public void setRectangle(Point startPoint, Point endPoint) {
        rectangle = new Rectangle(startPoint, new Dimension(endPoint.x - startPoint.x, endPoint.y - startPoint.y));
        System.out.println(rectangle);
    }

    public  void capture(String fileName, String extension) {
        try {
            new ScreenCapture().captureScreen(rectangle, fileName, extension);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
