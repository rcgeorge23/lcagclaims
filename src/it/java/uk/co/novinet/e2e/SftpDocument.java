package uk.co.novinet.e2e;

import java.time.Instant;

public class SftpDocument {
    private String filename;
    private String path;
    private Instant uploadDate;
    private Long size;

    public SftpDocument(String filename, String path, Instant uploadDate, Long size) {
        this.filename = filename;
        this.path = path;
        this.uploadDate = uploadDate;
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public Instant getUploadDate() {
        return uploadDate;
    }

    public Long getSize() {
        return size;
    }
}
