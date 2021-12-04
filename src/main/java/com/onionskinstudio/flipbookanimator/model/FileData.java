package com.onionskinstudio.flipbookanimator.model;

public class FileData {
    String name;        // flipbook name
    String fileImg;     // base64 encode of frame for thumbnail
    String filePath;    // URI of recent file

    public String getImgString() {
        return this.fileImg;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getName() {
        return this.name;
    }

}