package in.adarshr.screenrecorder;

import in.adarshr.screenrecorder.convert.VideoConverter;
import in.adarshr.screenrecorder.feature.ScreenRecording;
import in.adarshr.screenrecorder.util.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ScreenRecorder extends JFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenRecorder.class);
    static Properties properties;
    static ResourceBundle bundle;

    private JButton screenRecordingButtonStart;
    private JTextField fileNameField;
    private JComboBox<String> fileTypeComboBox;
    private final ScreenRecording screenRecordingCapture = new ScreenRecording();
    private File pendingMp4Output;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                properties = loadProperties("config/app.properties");
                bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ScreenRecorder frame = new ScreenRecorder(bundle, properties);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(560, 70);
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.error("Failed to create ScreenRecorder window.", e);
                JOptionPane.showMessageDialog(null,
                        "Failed to start ScreenRecorder: " + e.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static Properties loadProperties(String propFileName) {
        Properties prop = new Properties();
        try {
            InputStream classpathStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName);
            try (InputStream in = classpathStream != null ? classpathStream : new FileInputStream(propFileName)) {
                prop.load(in);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to load properties file: {}", propFileName, ex);
            JOptionPane.showMessageDialog(null,
                    "Failed to load configuration: " + propFileName + "\n" + ex.getMessage(),
                    "Startup error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return prop;
    }

    public ScreenRecorder(ResourceBundle bundle, Properties properties) {
        this.setName("ScreenRecorder");
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());

        fileNameField = new JTextField(20);
        fileNameField.setText(getFileName(null, properties));
        String[] videoFileTypes = properties.getProperty("videoFileTypes").split(",");
        fileTypeComboBox = new JComboBox<>(videoFileTypes);

        screenRecordingButtonStart = new JButton(bundle.getString("button.screenRecordingStart"));
        screenRecordingButtonStart.addActionListener(this);

        mainPanel.add(new JLabel(bundle.getString("label.fileName")));
        mainPanel.add(fileNameField);
        mainPanel.add(new JLabel(bundle.getString("label.fileType")));
        mainPanel.add(fileTypeComboBox);
        mainPanel.add(screenRecordingButtonStart);

        add(mainPanel, BorderLayout.CENTER);
    }

    private String getFileName(String fileName, Properties properties) {
        if (fileName == null || fileName.isBlank()) {
            String fileNameAsTime = properties.getProperty("timeFormat", "yyyyMMdd_HHmmssSSS");
            String fileNamePrefix = properties.getProperty("fileNamePrefix", "REC");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fileNameAsTime);
            return fileNamePrefix + simpleDateFormat.format(Calendar.getInstance().getTime());
        }
        return fileName;
    }

    private File resolveTargetDir() {
        String filePath = properties.getProperty("filePath");
        File dir;
        if (filePath == null || filePath.isBlank()) {
            dir = Paths.get("").toAbsolutePath().toFile();
        } else {
            dir = new File(filePath);
        }
        if (!dir.exists() && !dir.mkdirs()) {
            LOGGER.warn("Could not create output directory '{}', falling back to working dir", dir);
            dir = Paths.get("").toAbsolutePath().toFile();
        }
        return dir;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != screenRecordingButtonStart) {
            return;
        }
        String videoExt = String.valueOf(fileTypeComboBox.getSelectedItem());
        List<String> videoFileTypes = Arrays.stream(properties.getProperty("videoFileTypes").split(",")).toList();
        if (!videoFileTypes.contains(videoExt)) {
            return;
        }
        boolean isStart = bundle.getString("button.screenRecordingStart").equals(screenRecordingButtonStart.getText());
        if (isStart) {
            startRecording(videoExt);
        } else {
            stopRecording(videoExt);
        }
    }

    private void startRecording(String videoExt) {
        String baseName = getFileName(fileNameField.getText(), properties);
        File targetDir = resolveTargetDir();
        File finalOutput = new File(targetDir, baseName + "." + videoExt.toLowerCase(Locale.ROOT));
        File recordingFile;
        if ("MP4".equalsIgnoreCase(videoExt)) {
            recordingFile = new File(targetDir, baseName + ".tmp.avi");
            pendingMp4Output = finalOutput;
        } else {
            recordingFile = finalOutput;
            pendingMp4Output = null;
        }
        screenRecordingCapture.setPath(recordingFile.getAbsolutePath());
        if (!screenRecordingCapture.startRecording()) {
            pendingMp4Output = null;
            JOptionPane.showMessageDialog(this,
                    "Failed to start screen recording. See logs for details.",
                    "Recording error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        screenRecordingButtonStart.setText(bundle.getString("button.screenRecordingStop"));
        fileNameField.setEnabled(false);
        fileTypeComboBox.setEnabled(false);
        AppUtils.sleep(100L);
    }

    private void stopRecording(String videoExt) {
        screenRecordingCapture.stopRecording();
        screenRecordingButtonStart.setText(bundle.getString("button.screenRecordingStart"));

        if ("MP4".equalsIgnoreCase(videoExt) && pendingMp4Output != null) {
            File temp = new File(screenRecordingCapture.getPath());
            File finalOutput = pendingMp4Output;
            pendingMp4Output = null;
            screenRecordingButtonStart.setEnabled(false);
            screenRecordingButtonStart.setText(bundle.getString("button.converting"));
            new Thread(() -> runMp4Conversion(temp, finalOutput), "mp4-convert").start();
        } else {
            resetInputs();
        }
    }

    private void runMp4Conversion(File temp, File finalOutput) {
        VideoConverter converter = new VideoConverter(properties);
        boolean ok;
        try {
            ok = converter.convertToMp4(temp, finalOutput);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOGGER.error("MP4 conversion interrupted", ie);
            ok = false;
        } catch (Exception ex) {
            LOGGER.error("MP4 conversion failed", ex);
            ok = false;
        }
        boolean success = ok;
        SwingUtilities.invokeLater(() -> {
            screenRecordingButtonStart.setEnabled(true);
            screenRecordingButtonStart.setText(bundle.getString("button.screenRecordingStart"));
            resetInputs();
            if (success) {
                if (!temp.delete()) {
                    LOGGER.warn("Could not delete temp recording: {}", temp);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "MP4 conversion failed. The raw recording is at:\n" + temp.getAbsolutePath(),
                        "Conversion error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void resetInputs() {
        fileNameField.setEnabled(true);
        fileTypeComboBox.setEnabled(true);
        fileNameField.setText(getFileName(null, properties));
    }
}
