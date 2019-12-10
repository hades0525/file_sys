package com.cci.filemanage.repository;

import com.cci.filemanage.entity.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kai
 */
@Repository
public class FileManageRepository {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<FileMetadata> queryFileMetadataByBizId(String businessKey) {
        String sql = "select a.*\n" +
                "from file_metadata a,\n" +
                "     file_relation_ship b\n" +
                "where a.id = b.file_id\n" +
                "and b.biz_id=?";

        List<FileMetadata> metadataList = jdbcTemplate.query(sql, FileMetadata::createFileMetadata, businessKey);

        return metadataList;
    }

}