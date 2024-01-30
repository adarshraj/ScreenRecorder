package in.adarshr.screenrecorder.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VideoConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoConverter.class);
    Properties prop;
    private String inputPath;
    private String outputPath;
    public VideoConverter() {
        prop = loadProperties("config/app.properties");
    }

    public  Properties loadProperties(String propFileName) {
        Properties prop = new Properties();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName)) {
            prop.load(inputStream);
        } catch (IOException ex) {
            LOGGER.error("Failed to create ScreenRecorder window.", ex);
        }
        return prop;
    }

    public  void convertMovToMp4() {
        String input = getInputPath() != null ? getInputPath() : prop.getProperty("input_path");
        String output = getOutputPath() != null ? getOutputPath() : prop.getProperty("output_path");
        ProcessBuilder pb = new ProcessBuilder(
                prop.getProperty("ffmpeg_path"),
                prop.getProperty("input_file_option"),
                input,
                prop.getProperty("video_codec_option"),
                prop.getProperty("h264_codec"),
                prop.getProperty("audio_codec_option"),
                prop.getProperty("mp2_codec"),
                output
        );
        try {
            Process pc = pb.start();
             pc.waitFor();
        } catch (IOException | InterruptedException ex) {
            LOGGER.error("Failed to create ScreenRecorder window.", ex);
        }
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
