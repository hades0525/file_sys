package com.cci.filemanage.service.impl;

import com.cci.filemanage.entity.FileMetadata;
import com.cci.filemanage.model.FileEntity;
import com.cci.filemanage.repository.FileMetadatRepository;
import com.cci.filemanage.repository.FileRelationShipRepository;
import com.cci.filemanage.utils.IdUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @author yangkai
 * @date 2019/6/25
 * @since 1.0
 */
@Component
public class FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    @Autowired
    private FileMetadatRepository metadatRepository;

    @Autowired
    private FileRelationShipRepository relationShipRepository;

    @Value("${file.upload.basedir}")
    private String basePath;

    public FileMetadata put(MultipartFile multipartFile) {

        OutputStream out = null;
        try {

            String filename = multipartFile.getOriginalFilename();
            String id = IdUtils.getId();
            String newFilename = String.format("%s.%s", id, FilenameUtils.getExtension(filename));

            FileMetadata fileMetaData = new FileMetadata();
            fileMetaData.setId(id);
            fileMetaData.setFilename(newFilename);
            fileMetaData.setOriginalFilename(multipartFile.getOriginalFilename());
            fileMetaData.setContentType(multipartFile.getContentType());
            fileMetaData.setFileSize(multipartFile.getSize());
            fileMetaData.setFilePath(FileManagerHelper.getFilePath(newFilename));
            fileMetaData.setCreateTime(new Date());

            LOGGER.info("File metadata:{}", fileMetaData);

            Path fileFullPath = Paths.get(basePath, fileMetaData.getFilePath());

            File parent = fileFullPath.getParent().toFile();

            if (!parent.exists()) {
                parent.mkdirs();
            }

            out = Files.newOutputStream(fileFullPath);
            IOUtils.write(multipartFile.getBytes(), out);

            metadatRepository.insert(fileMetaData);

            return fileMetaData;

        } catch (IOException e) {
            LOGGER.error("Put file error.", e);
        } finally {
            if (out != null) {
                IOUtils.closeQuietly(out);
            }
        }

        return null;
    }

    public FileMetadata head(String id) {
        if (StringUtils.isEmpty(id)) {
            LOGGER.warn("File id is empty.");
            throw new IllegalArgumentException("File id is empty.");
        }

        FileMetadata fileMetadata = metadatRepository.get(id);

        LOGGER.info("head filemetadata:{}", fileMetadata);

        return fileMetadata;
    }

    public FileEntity get(String id) throws FileNotFoundException {
        FileMetadata fileMetadata = this.head(id);
        Path fileFullPath = this.getFullPath(id);
        try (InputStream in = Files.newInputStream(fileFullPath)) {

            byte[] content = IOUtils.toByteArray(in);

            LOGGER.info("content size:{}", content.length);

            return new FileEntity(fileMetadata, content);

        } catch (FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("get file error:" + id, e);
        }

        return null;
    }

    public FileMetadata deleteFile(String fileId) {
        FileMetadata metadata = null;
        try {
            metadata = this.head(fileId);

            Path fileFullPath = this.getFullPath(fileId);

            metadatRepository.delete(fileId);
            relationShipRepository.delete(fileId);

            if (fileFullPath.toFile().exists()) {
                fileFullPath.toFile().delete();
            }

            return metadata;

        } catch (FileNotFoundException e) {
            LOGGER.error("Delete file failed: file not exists, fileId: " + fileId);
        } catch (Exception e) {
            LOGGER.error("Delete file failed: fileId: " + fileId, e);
        }

        return metadata;

    }


    private void copyFile(FileMetadata source, FileMetadata dest) {

        if (source == null || dest == null) {
            LOGGER.warn("failed to copy file, source file or dest file is null.");
            return;
        }


        Path sourcePath = Paths.get(basePath, source.getFilePath());
        Path destPath = Paths.get(basePath, dest.getFilePath());

        InputStream in = null;
        OutputStream out = null;

        try {

            in = Files.newInputStream(sourcePath);
            out = Files.newOutputStream(destPath);
            IOUtils.copy(in, out);

        } catch (IOException e) {
            LOGGER.error("Copy file error:", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

    }

    private Path getFullPath(String id) throws FileNotFoundException {

        FileMetadata fileMetadata = this.head(id);

        if (fileMetadata == null) {
            LOGGER.error("Get FileMetadata by id({}) is null.", id);
            throw new NullPointerException("FileMetadata is null by id " + id);
        }

        Path fileFullPath = Paths.get(basePath, fileMetadata.getFilePath());
        if (!fileFullPath.toFile().exists()) {
            LOGGER.warn("File not exist:{}", fileFullPath);
            throw new FileNotFoundException("File not exists, id: " + id);
        }

        return fileFullPath;
    }

    public static class FileManagerHelper {

        private static final String DATE_PATTERN = "yyyy/MM/dd";

        public static String getFilePath(String filename) {
            String dateStr = DateFormatUtils.format(System.currentTimeMillis(), DATE_PATTERN);
            return String.format("%s/%s", dateStr, filename);
        }

    }


}
