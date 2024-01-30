package in.adarshr.screenrecorder;

import in.adarshr.screenrecorder.convert.VideoConverter;
import in.adarshr.screenrecorder.feature.FullScreenCapture;
import in.adarshr.screenrecorder.feature.ScreenRecording;
import in.adarshr.screenrecorder.feature.UserDefinedCapture;
import in.adarshr.screenrecorder.util.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ScreenRecorder extends JFrame implements ActionListener, KeyListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenRecorder.class);
    static Properties properties;
    static ResourceBundle bundle;
    JButton fullScreenshotButton;
    JButton selectedScreenshotButton;
    JButton screenRecordingButtonStart;
    JTextField fileNameField;
    JComboBox<String> fileTypeComboBox;
    ScreenRecording screenRecordingCapture = new ScreenRecording();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                //Load application properties
                properties = loadProperties("app.properties");
                // Load resources
                Locale locale = Locale.of("en");
                bundle = ResourceBundle.getBundle("messages", locale);

                // Create frame
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ScreenRecorder frame = new ScreenRecorder(bundle, properties);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(800, 70);
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
        try  {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName);
            if(inputStream == null){
                LOGGER.error("Couldn't load the app.properties file through method 1");
                inputStream = new FileInputStream(propFileName);
            }
            prop.load(inputStream);
        } catch (IOException ex) {
            LOGGER.error("Failed to load properties file.", ex);
        }

        return prop;
    }

    public ScreenRecorder(ResourceBundle bundle, Properties properties) {
        this.setName("ScreenRecorder");
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());

        // File options
        fileNameField = new JTextField(20);
        fileNameField.setText(getFileName(null, properties));
        String[] imageFileTypes = properties.getProperty("imageFileTypes").split(",");
        String[] videoFileTypes = properties.getProperty("videoFileTypes").split(",");
        fileTypeComboBox = new JComboBox<>(AppUtils.mergeArray(imageFileTypes, videoFileTypes));

        // Buttons
        fullScreenshotButton = new JButton(bundle.getString("button.fullScreenshot"));
        fullScreenshotButton.addActionListener(this);
        selectedScreenshotButton = new JButton(bundle.getString("button.selectedScreenshot"));
        selectedScreenshotButton.addActionListener(this);
        screenRecordingButtonStart = new JButton(bundle.getString("button.screenRecordingStart"));
        screenRecordingButtonStart.addActionListener(this);

        mainPanel.add(new JLabel(bundle.getString("label.fileName")));
        mainPanel.add(fileNameField);
        mainPanel.add(new JLabel(bundle.getString("label.fileType")));
        mainPanel.add(fileTypeComboBox);
        mainPanel.add(fullScreenshotButton);
        mainPanel.add(selectedScreenshotButton);
        mainPanel.add(screenRecordingButtonStart);

        add(mainPanel, BorderLayout.CENTER);
    }

    private String getFilePath(String fileName, String extension, Properties properties) {
        String filePath = properties.getProperty("filePath");
        if (filePath == null || filePath.isBlank()) {
            Path currentRelativePath = Paths.get("");
            filePath = currentRelativePath.toAbsolutePath() + "\\";
        }
        return filePath + getFileName(fileName, properties) + getExtension(extension, properties);
    }

    private String getExtension(String extension, Properties properties) {
        if (extension == null || extension.isBlank()) {
            return "." + properties.getProperty("defaultImageExtension", "jpg");
        }
        return "." + extension;
    }

    private String getFileName(String fileName, Properties properties) {
        String fileNameFormatted;
        if (fileName == null || fileName.isBlank()) {
            String fileNameAsTime = properties.getProperty("timeFormat", "yyyyMMdd_hhmmssSSS_a");
            String fileNamePrefix = properties.getProperty("imageFileNamePrefix", "IMG");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fileNameAsTime);
            fileNameFormatted = fileNamePrefix + simpleDateFormat.format(Calendar.getInstance().getTime());
            return fileNameFormatted;
        }
        return fileName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fullScreenshotButton) {
            SwingUtilities.invokeLater(() -> {
                this.setVisible(false);
                AppUtils.sleep(200L);
                new FullScreenCapture().
                        capture(getFilePath(fileNameField.getText(), Objects.requireNonNull(fileTypeComboBox.getSelectedItem()).toString(), properties), fileTypeComboBox.getSelectedItem().toString());
                this.setVisible(true);
            });
        } else if (e.getSource() == selectedScreenshotButton) {
            SwingUtilities.invokeLater(() -> {
                this.setVisible(false);
                UserDefinedCapture userDefinedCapture = new UserDefinedCapture();
                userDefinedCapture.setVisible(true);
                userDefinedCapture.setFilePath(getFilePath(fileNameField.getText(), Objects.requireNonNull(fileTypeComboBox.getSelectedItem()).toString(), properties));
                userDefinedCapture.setFileExtension(fileTypeComboBox.getSelectedItem().toString());
            });
        } else if (e.getSource() == screenRecordingButtonStart) {
            SwingUtilities.invokeLater(() -> {
                String videoExt = String.valueOf(fileTypeComboBox.getSelectedItem());
                List<String> videoFileTypes = Arrays.stream(properties.getProperty("videoFileTypes").split(",")).toList();
                if(videoFileTypes.contains(videoExt)) {
                    String recordingBtnText = screenRecordingButtonStart.getText();
                    String fileName = getFilePath(fileNameField.getText(), Objects.requireNonNull(fileTypeComboBox.getSelectedItem()).toString(), properties);
                    screenRecordingCapture.setPath(fileName);
                    if (bundle.getString("button.screenRecordingStart").equals(recordingBtnText)) {
                        screenRecordingButtonStart.setText(bundle.getString("button.screenRecordingStop"));
                        screenRecordingCapture.startRecording();
                        AppUtils.sleep(100L);
                    } else {
                        screenRecordingCapture.stopRecording();
                        screenRecordingButtonStart.setText(bundle.getString("button.screenRecordingStart"));
                        if("MP4".equals(videoExt)){
                            VideoConverter videoConverter = new VideoConverter();
                            videoConverter.setInputPath(fileName);
                            videoConverter.setOutputPath(fileName);
                            videoConverter.convertMovToMp4();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}