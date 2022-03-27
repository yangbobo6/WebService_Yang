package com.yangbo.webserver.core.utils;

import eu.medsea.mimeutil.MimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import static com.yangbo.webserver.core.constant.ContextConstant.DEFAULT_CONTENT_TYPE;
/**
 * @Author: yangbo
 * @Date: 2022-03-27-16:06
 * @Description:
 */
@Slf4j
public class MimeTypeUtil {
    static {
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }

    public static String getTypes(String fileName) {
        if(fileName.endsWith(".html")){
            return DEFAULT_CONTENT_TYPE;
        }
        Collection mimeTypes = MimeUtil.getMimeTypes(MimeTypeUtil.class.getResource(fileName));
        return mimeTypes.toArray()[0].toString();
    }

}
