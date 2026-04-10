package in.adarshr.screenrecorder.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class VideoConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoConverter.class);
    private final Properties properties;

    public VideoConverter(Properties properties) {
        this.properties = properties;
    }

    /**
     * Convert {@code input} to H.264/MP2 MP4 at {@code output}. Input and output
     * must be distinct paths. Returns true on a clean ffmpeg exit.
     */
    public boolean convertToMp4(File input, File output) throws IOException, InterruptedException {
        if (input.equals(output)) {
            throw new IllegalArgumentException("input and output must differ: " + input);
        }
        String ffmpeg = properties.getProperty("ffmpeg_path");
        if (ffmpeg == null || ffmpeg.isBlank()) {
            ffmpeg = "ffmpeg";
        }
        String videoCodec = properties.getProperty("h264_codec", "h264");
        String audioCodec = properties.getProperty("mp2_codec", "mp2");

        ProcessBuilder pb = new ProcessBuilder(
                ffmpeg,
                "-y",
                "-i", input.getAbsolutePath(),
                "-vcodec", videoCodec,
                "-acodec", audioCodec,
                output.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process pc = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.debug("ffmpeg: {}", line);
            }
        }
        int code = pc.waitFor();
        if (code != 0) {
            LOGGER.error("ffmpeg exited with code {} for {} -> {}", code, input, output);
            return false;
        }
        return true;
    }
}
