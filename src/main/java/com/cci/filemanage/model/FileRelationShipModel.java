package com.cci.filemanage.model;

import java.util.List;

/**
 * @author yangkai
 * @date 2019/07/22
 * @since 1.0
 */
public interface FileRelationShipModel {

    /**
     * 返回业务主键ID
     * @return String 业务主键ID
     */
    String getBusinessKey();

    /**
     * 返回文件ID列表
     * @return List 文件ID列表
     */
    List<String> getFileId();

}
