package com.yangbo.webserver.core.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;


import java.io.InputStream;

/**
 * @Author: yangbo
 * @Date: 2022-03-24-10:21
 * @Description:
 */
public class XMLUtil {
    public static Document getDocument(InputStream in){
        try {
            SAXReader reader = new SAXReader();
            return reader.read(in);
        }catch (DocumentException e){
            e.printStackTrace();
        }
        return null;
    }
}
