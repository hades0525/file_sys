package com.cci.filemanage.service.impl;

import com.cci.filemanage.entity.FileMetadata;
import com.cci.filemanage.entity.FileRelationShip;
import com.cci.filemanage.model.FileEntity;
import com.cci.filemanage.model.FileRelationShipModel;
import com.cci.filemanage.repository.FileManageRepository;
import com.cci.filemanage.repository.FileRelationShipRepository;
import com.cci.filemanage.service.FileManageService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangkai
 * @date 2019/06/26
 * @since 1.0
 */
@Component
public class FileManageServiceImpl implements FileManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManageServiceImpl.class);

    @Autowired
    private FileRelationShipRepository fileRelationShipRepository;

    @Autowired
    private FileManageRepository fileManageMapper;

    @Autowired
    private FileManager fileManager;

    @Value("${file.upload.basedir}")
    private String basePath;

    @Override
    public FileMetadata put(MultipartFile multipartFile) {
        return fileManager.put(multipartFile);
    }

    @Override
    public FileMetadata head(String id) {
        return fileManager.head(id);
    }

    @Override
    public FileEntity get(String id) throws FileNotFoundException {
        return fileManager.get(id);
    }

    @Override
    public FileMetadata delete(String id) {
        return fileManager.deleteFile(id);
    }

    @Override
    public List<FileMetadata> findFiles(String bizId) {
        return fileManageMapper.queryFileMetadataByBizId(bizId);
    }

    @Override
    public void createFileRelationShip(FileRelationShipModel fileModel) {
        for (String fileId : fileModel.getFileId()) {
            this.createFileRelationShip(fileModel.getBusinessKey(), fileId);
        }
    }

    private FileRelationShip createFileRelationShip(String bizId, String fileId) {
        FileRelationShip relationShip = new FileRelationShip(bizId, fileId);
        fileRelationShipRepository.insert(relationShip);
        return relationShip;
    }

    private List<FileRelationShip> findFileRelationShip(String businessKey) {
        return fileRelationShipRepository.findFileRelationShip(businessKey);
    }

    public int addFiles(FileRelationShipModel fileModel) {
        return this.addFiles(fileModel.getBusinessKey(), fileModel.getFileId());
    }

    private void addFile(String businessKey, String fileId) {
        fileRelationShipRepository.insert(this.createFileRelationShip(businessKey, fileId));
    }

    private int addFiles(String businessKey, List<String> fileIds) {

        if (StringUtils.isEmpty(businessKey)) {
            LOGGER.warn("Failed to add files, businessKey is empty.");
            return 0;
        }

        if (CollectionUtils.isEmpty(fileIds)) {
            LOGGER.warn("Failed to add files, fileIds is empty.");
            return 0;
        }

        List<String> newFileIds = new ArrayList<>(fileIds);
        List<String> existFileIds;

        List<FileRelationShip> fileRelationShipList = this.findFileRelationShip(businessKey);
        if (!CollectionUtils.isEmpty(fileRelationShipList)) {
            existFileIds = fileRelationShipList.stream().map(FileRelationShip::getFileId).collect(Collectors.toList());
            newFileIds.removeAll(existFileIds);

            LOGGER.info("Exist files:{}", StringUtils.join(existFileIds, ","));
        }

        if (CollectionUtils.isEmpty(newFileIds)) {
            LOGGER.warn("No new file to add, the newFileIds is empty.");
        }

        newFileIds.forEach(fileId -> this.addFile(businessKey, fileId));

        return newFileIds.size();

    }


}
