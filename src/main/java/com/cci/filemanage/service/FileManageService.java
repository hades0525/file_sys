package com.cci.filemanage.service;

import com.cci.filemanage.entity.FileMetadata;
import com.cci.filemanage.model.FileEntity;
import com.cci.filemanage.model.FileRelationShipModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * 文件管理服务类。
 * 负责文件存储和读取。
 *
 * @author yangkai
 * @date 2019/06/26
 * @since 1.0
 */
public interface FileManageService {

    /**
     * 获取文件对象，包括文件元数据和文件对象
     *
     * @param id 文件ID
     * @return 文件对象
     * @throws FileNotFoundException 文件不存在异常
     */
    FileEntity get(String id) throws FileNotFoundException;

    /**
     * 存储文件到本地文件系统
     *
     * @param multipartFile
     * @return
     */
    FileMetadata put(MultipartFile multipartFile);

    /**
     * 获取文件的元数据
     *
     * @param id 文件ID
     * @return 文件元数据
     */
    FileMetadata head(String id);

    /**
     * 删除文件，包括文件元数据和文件对象
     *
     * @param id String 文件ID
     * @return
     * @throws FileNotFoundException
     */
    FileMetadata delete(String id) throws FileNotFoundException;

    /**
     * 根据业务ID查询所有文件元数据
     *
     * @param businessKey 业务ID
     * @return 文件元数据列表
     */
    List<FileMetadata> findFiles(String businessKey);

    /**
     * 建立业务-文件之间的关联关系
     *
     * @param fileModel
     */
    void createFileRelationShip(FileRelationShipModel fileModel);

}
