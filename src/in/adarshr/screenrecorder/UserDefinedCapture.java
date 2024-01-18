package in.adarshr.screenrecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDefinedCapture {

    private static final Logger LOGGER = Logger.getLogger(UserDefinedCapture.class.getName());

    private JFrame frame;
    private Point startPoint;

    public UserDefinedCapture() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setOpacity(1.0f); // Making the window transparent
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen window
        frame.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, true)); // Set panel transparent
        panel.setBounds(0, 0, frame.getWidth()-100, frame.getHeight()-100);
        frame.getContentPane().add(panel);

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startPoint = e.getPoint(); // Get the start point when mouse is pressed
                System.out.println("mouseClicked");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint(); // Get the start point when mouse is pressed
                System.out.println(e.getPoint());
                System.out.println("mousePressed");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("mouseReleased");
                System.out.println(e.getPoint());
                captureArea(startPoint, e.getPoint()); // Capture the area on mouse release

                frame.dispose(); // Close the frame after capturing
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("mouseEntered");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                System.out.println("mouseExited");
            }
        });
/*        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint(); // Get the start point when mouse is pressed
                System.out.println(e.getPoint());
            }

            public void mouseReleased(MouseEvent e) {
                System.out.println(e.getPoint());
                captureArea(startPoint, e.getPoint()); // Capture the area on mouse release

                frame.dispose(); // Close the frame after capturing
            }
        });*/

        frame.setVisible(true);
    }

    private void captureArea(Point start, Point end) {
        try {
            new Robot();
            int width = end.x - start.x;
            int height = end.y - start.y;
            Rectangle area = new Rectangle(start.x, start.y, width, height);
            //BufferedImage bufferedImage = robot.createScreenCapture(area);

            // Here, you can save the image or do something else with it
            // For example, save it to a file:
            // ImageIO.write(bufferedImage, "png", new File("captured_area.png"));
            System.out.println(area);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error Log", ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserDefinedCapture::new);
    }
}

