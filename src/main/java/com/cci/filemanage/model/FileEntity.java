package com.cci.filemanage.model;

import com.cci.filemanage.entity.FileMetadata;

public class FileEntity {

    private FileMetadata fileMetadata;

    private byte[] content;

    public FileEntity(FileMetadata fileMetadata, byte[] content) {
        this.fileMetadata = fileMetadata;
        this.content = content;
    }

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public byte[] getContent() {
        return content;
    }
}