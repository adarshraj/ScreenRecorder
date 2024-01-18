package in.adarshr.screenrecorder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VideoConverter {

    public static void main(String[] args) {
        Properties prop = loadProperties("app.properties");

        convertMovToMp4(prop);
    }

    public static Properties loadProperties(String propFileName) {
        Properties prop = new Properties();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName)) {
            prop.load(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return prop;
    }

    public static void convertMovToMp4(Properties prop) {
        ProcessBuilder pb = new ProcessBuilder(
                prop.getProperty("ffmpeg_path"),
                prop.getProperty("input_file_option"),
                prop.getProperty("input_path"),
                prop.getProperty("video_codec_option"),
                prop.getProperty("h264_codec"),
                prop.getProperty("audio_codec_option"),
                prop.getProperty("mp2_codec"),
                prop.getProperty("output_path")
        );

        try {
            Process pc = pb.start();
            try {
                pc.waitFor();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
