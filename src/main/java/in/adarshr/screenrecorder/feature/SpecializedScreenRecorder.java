package in.adarshr.screenrecorder.feature;

import org.monte.media.Format;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

class SpecializedScreenRecorder extends ScreenRecorder {
    private String path;
    public SpecializedScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                                     Format screenFormat, Format mouseFormat, Format audioFormat)
            throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat);
    }

    @Override
    protected File createMovieFile(Format fileFormat) {
        //return new File("C:\\test\\ScreenRecordingTest.mov");
        return new File(getPath());
    }

    private String getPath(){
        return this.path;
    }

    public void setPath(String path){
        this.path=path;
    }
}
