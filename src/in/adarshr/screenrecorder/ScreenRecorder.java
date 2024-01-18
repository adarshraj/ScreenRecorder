package in.adarshr.screenrecorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ScreenRecorder extends JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenRecorder.class);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Properties properties = loadProperties("app.properties");
                // Load resources
                Locale locale = Locale.of("en");
                ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

                // Create frame
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ScreenRecorder frame = new ScreenRecorder(bundle, properties);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(800, 100);
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.error("Failed to create ScreenRecorder window.", e);
            }
        });
    }

    public static Properties loadProperties(String propFileName) {
        Properties prop = new Properties();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName)) {
            prop.load(inputStream);
        } catch (IOException ex) {
            LOGGER.error("Failed to load properties file.", ex);
        }

        return prop;
    }

    public ScreenRecorder(ResourceBundle bundle, Properties properties) {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());

        // File options
        JTextField fileNameField = new JTextField(20);
        String[] fileTypes = properties.getProperty("fileTypes").split(",");
        JComboBox<String> fileTypeComboBox = new JComboBox<>(fileTypes);

        // Buttons
        JButton fullScreenshotButton = new JButton(bundle.getString("button.fullScreenshot"));
        JButton selectedScreenshotButton = new JButton(bundle.getString("button.selectedScreenshot"));
        JButton screenRecordingButton = new JButton(bundle.getString("button.screenRecording"));
        JButton optionsButton = new JButton(bundle.getString("button.options"));

        mainPanel.add(new JLabel(bundle.getString("label.fileName")));
        mainPanel.add(fileNameField);
        mainPanel.add(new JLabel(bundle.getString("label.fileType")));
        mainPanel.add(fileTypeComboBox);
        mainPanel.add(fullScreenshotButton);
        mainPanel.add(selectedScreenshotButton);
        mainPanel.add(screenRecordingButton);
        mainPanel.add(optionsButton);

        add(mainPanel, BorderLayout.CENTER);
    }
}