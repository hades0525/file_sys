package com.cci.filemanage.repository;

import com.cci.filemanage.entity.FileRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kai
 */
@Repository
public class FileRelationShipRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    public void insert(FileRelationShip relationShip) {
        jdbcTemplate.update("insert into file_relation_ship(biz_id, file_id) values(:bizId, :fileId)", relationShip.toMap());
    }

    public void delete(String fileId) {
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("fileId", fileId);
        jdbcTemplate.update("delete from file_relation_ship where file_id=:fileId", paramMap);
    }

    public List<FileRelationShip> findFileRelationShip(String businessKey) {
        String sql = "select * from file_relation_ship where biz_id=:businessKey";

        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("businessKey", businessKey);

        List<FileRelationShip> relationShips = jdbcTemplate.query(sql, paramMap, FileRelationShip::createFileRelationShip);
        return relationShips;
    }
}