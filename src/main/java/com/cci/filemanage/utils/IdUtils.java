package com.cci.filemanage.utils;

import java.util.UUID;

/**
 * @author yangkai
 * @date 2019/11/15 22:12
 * @since 1.0
 */
public class IdUtils {
    public static String getId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
