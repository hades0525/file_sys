package com.cci.filemanage.entity;


import lombok.Data;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kai
 */
@Data
@ToString
public class FileRelationShip {
    private String id;
    private String bizId;
    private String fileId;

    public FileRelationShip() {
    }

    public FileRelationShip(String bizId, String fileId) {
        this.bizId = bizId;
        this.fileId = fileId;
    }

    public static FileRelationShip createFileRelationShip(ResultSet rs, int rowNum) throws SQLException {
        FileRelationShip metadata = new FileRelationShip();
        metadata.setId(rs.getString("id"));
        metadata.setBizId(rs.getString("bizId"));
        metadata.setFileId(rs.getString("fileId"));
        return metadata;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("bizId", bizId);
        map.put("fileId", fileId);
        return map;
    }

}