package com.cci.filemanage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MainApp {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        log.warn("start file_manage app ...");
        try {
            SpringApplication.run(MainApp.class, args);

            long cast = System.currentTimeMillis() - start;
            log.warn("file_manage app start success, cast {}ms.", cast);

        } catch (Exception e) {
            log.error("file_manage app start failed.", e);
        }
    }

}
