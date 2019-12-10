package com.cci.filemanage.repository;

import com.cci.filemanage.entity.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kai
 */
@Repository
public class FileMetadatRepository {

    private static final String INSERT_SQL = "insert into file_metadata(id, filename, original_filename, file_size, file_path, " +
            "content_type, create_time, create_user, remark)" +
            "values(:id, :filename, :originalFilename, :fileSize, :filePath, " +
            ":contentType, :createTime, :createUser, :remark)";

    private static final String SELECT_SQL = "select * from file_metadata where id = :id";

    private static final String DELETE_SQL = "delete from file_metadata where id = :id ";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public FileMetadata get(String id) {

        Map<String, String> paramMap = new HashMap<>(1);
        paramMap.put("id", id);

        List<FileMetadata> query = this.jdbcTemplate.query(SELECT_SQL, paramMap, FileMetadata::createFileMetadata);

        if (CollectionUtils.isEmpty(query)) {
            return null;
        }

        return query.get(0);
    }

    public int insert(FileMetadata metadata) {
        return jdbcTemplate.update(INSERT_SQL, metadata.toParamMap());
    }

    public int delete(String id) {
        Map<String, String> paramMap = new HashMap<>(1);
        paramMap.put("id", id);
        return jdbcTemplate.update(DELETE_SQL, paramMap);
    }


}