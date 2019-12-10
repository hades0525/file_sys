package com.cci.filemanage.controller;

import com.cci.filemanage.entity.FileMetadata;
import com.cci.filemanage.model.DefaultFileRelationShipRelationShip;
import com.cci.filemanage.model.FileEntity;
import com.cci.filemanage.service.FileManageService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 *
 * 文件上传处理
 *
 * @author yangkai
 * @date 2019/6/25
 * @since 1.0
 */
@RestController
@RequestMapping("/")
public class FileManageController {
    @Value("${file_addr}")
    private String fileAddr;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManageController.class);

    private static final int HTTP_DEFAULT_PORT = 80;

    private static final int HTTPS_DEFAULT_PORT = 443;

    @Autowired
    private FileManageService fileManageService;

    @PostMapping(value = "file")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            FileMetadata fileMetadata = fileManageService.put(file);
            fileMetadata.setUrl(fileAddr+"/show/"+fileMetadata.getId());
            return new ResponseEntity(fileMetadata, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("upload file error", e);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "file/{id}", method = {RequestMethod.GET})
    public ResponseEntity getFileMetadata(@PathVariable(value = "id") String id, HttpServletRequest request) {
        FileMetadata metadata = this.fileManageService.head(id);
        metadata.setUrl(fileAddr+"/show/"+id);
        return new ResponseEntity(metadata, HttpStatus.OK);
    }

    @DeleteMapping(value = "file/{id}")
    public ResponseEntity delete(@PathVariable(value = "id") String id) {
        try {
            fileManageService.delete(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (FileNotFoundException e) {
            LOGGER.error("Delete file error, fileId:" + id, e);
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("download/{id}")
    public void export(@PathVariable(value = "id") String id, HttpServletResponse response) {

        try {
            FileEntity entity = fileManageService.get(id);

            String encodeFilename = URLEncoder.encode(entity.getFileMetadata().getOriginalFilename(), "utf-8");
            String contentDisposition = String.format("attachment; filename=%s", encodeFilename);

            response.setContentType(entity.getFileMetadata().getContentType());
            response.setContentLength(Math.toIntExact(entity.getFileMetadata().getFileSize()));
            response.setHeader("content-disposition", contentDisposition);
            response.setStatus(HttpStatus.OK.value());

            response.getOutputStream().write(entity.getContent());

        } catch (IOException e) {
            LOGGER.error("Download file error, fileId:" + id, e);
        }

    }

    @GetMapping("show/{id}")
    public void show(@PathVariable("id") String id, HttpServletResponse response) {

        try {
            FileEntity entity = fileManageService.get(id);

            response.setContentType(entity.getFileMetadata().getContentType());
            response.setContentLength(Math.toIntExact(entity.getFileMetadata().getFileSize()));
            response.setStatus(HttpStatus.OK.value());

            response.getOutputStream().write(entity.getContent());

        } catch (IOException e) {
            LOGGER.error("Show file error, fileId:" + id, e);
        }
    }

    @GetMapping("downFile")
    public void exportFile(String id, HttpServletResponse response) {
        this.export(id, response);
    }

    @PostMapping("addRelation")
    public ResponseEntity createFileRelationShip(@RequestBody DefaultFileRelationShipRelationShip relationShip) {

        LOGGER.info("relationShip:{}", relationShip);

        HttpStatus httpStatus;

        try {

            this.fileManageService.createFileRelationShip(relationShip);
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            LOGGER.warn("add file relation ship error:", e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity(httpStatus);
    }

    @GetMapping("listFiles/{businessKey}")
    public ResponseEntity getFilesByBusinessKey(@PathVariable(value = "businessKey") String businessKey) {
        List<FileMetadata> files = fileManageService.findFiles(businessKey);
        return new ResponseEntity(files, HttpStatus.OK);
    }

    private String getUrl(HttpServletRequest request, String id) {
        StringBuilder sb = new StringBuilder();

        sb.append(request.getScheme()).append("://").append(request.getServerName());

        boolean isHttpDefaultPort = "http".equalsIgnoreCase(request.getScheme()) && request.getServerPort() != HTTP_DEFAULT_PORT;
        boolean isHttpsDefaultPort = "https".equalsIgnoreCase(request.getScheme()) && request.getServerPort() != HTTPS_DEFAULT_PORT;

        if (!isHttpDefaultPort || !isHttpsDefaultPort) {
            sb.append(":").append(request.getServerPort());
        }

        if (StringUtils.isNotEmpty(request.getContextPath())) {
            sb.append("/").append(request.getContextPath());
        }

        sb.append("/show/").append(id);

        return sb.toString();
    }


}
