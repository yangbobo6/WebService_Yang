package com.yangbo.webserver.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: yangbo
 * @Date: 2022-03-24-15:31
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private String key;
    private String value;
}
