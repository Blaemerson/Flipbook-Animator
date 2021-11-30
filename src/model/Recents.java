package model;

import java.util.LinkedList;
import java.util.List;

public class Recents {

    List<FileData> recentFiles = new LinkedList<>();

    public List<FileData> getRecentFiles() {
        return this.recentFiles;
    }

    private class FileData {
        String fileImg;   // base64 encode of frame for thumbnail
        String filePath;    // URI of recent file

        public String getImgString() {
            return this.fileImg;
        }

        public String getFilePath() {
            return this.filePath;
        }
    }
}
