package in.adarshr.screenrecorder.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppUtils {
    public static String[] mergeArray(String[] array1, String[] array2) {
        List<String> list = new ArrayList<>(Arrays.asList(array1));
        list.addAll(Arrays.asList(array2));
        return list.toArray(new String[0]);
    }

    public static void sleep(Long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
