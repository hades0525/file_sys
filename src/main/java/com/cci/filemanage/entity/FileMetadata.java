package com.cci.filemanage.entity;

import lombok.Data;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class FileMetadata {
    private String id;
    private String filename;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private String createUser;
    private Date createTime;
    private String remark;

    /**
     * 文件访问的URL地址
     * <p>
     * 非数据库字段
     */
    private String url;

    public static FileMetadata createFileMetadata(ResultSet rs, int rowNum) throws SQLException {
        FileMetadata metadata = new FileMetadata();
        metadata.setId(rs.getString("id"));
        metadata.setCreateTime(rs.getDate("create_time"));
        metadata.setContentType(rs.getString("content_type"));
        metadata.setCreateUser(rs.getString("create_user"));
        metadata.setFilename(rs.getString("filename"));
        metadata.setFilePath(rs.getString("file_path"));
        metadata.setOriginalFilename(rs.getString("original_filename"));
        metadata.setFileSize(rs.getLong("file_size"));
        metadata.setRemark(rs.getString("remark"));
        return metadata;
    }

    public Map<String, Object> toParamMap() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", this.getId());
        paramMap.put("filename", this.getFilename());
        paramMap.put("originalFilename", this.getOriginalFilename());
        paramMap.put("contentType", this.getContentType());
        paramMap.put("fileSize", this.getFileSize());
        paramMap.put("filePath", this.getFilePath());
        paramMap.put("createUser", this.getCreateUser());
        paramMap.put("createTime", this.getCreateTime());
        paramMap.put("remark", this.getRemark());
        return paramMap;
    }

}