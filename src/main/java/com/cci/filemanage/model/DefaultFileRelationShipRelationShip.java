package com.cci.filemanage.model;

import lombok.ToString;

import java.util.List;

/**
 * @author yangkai
 * @date 2019/11/16 15:54
 * @since 1.0
 */
@ToString
public class DefaultFileRelationShipRelationShip implements FileRelationShipModel {

    private String businessKey;

    private List<String> fileId;

    @Override
    public String getBusinessKey() {
        return this.businessKey;
    }

    @Override
    public List<String> getFileId() {
        return this.fileId;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public void setFileId(List<String> fileId) {
        this.fileId = fileId;
    }
}
