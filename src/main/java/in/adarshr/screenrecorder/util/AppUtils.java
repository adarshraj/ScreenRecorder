package in.adarshr.screenrecorder.util;

public class AppUtils {
    public static void sleep(Long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
}
