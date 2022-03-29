package com.yangbo.webserver.sample.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: yangbo
 * @Date: 2022-03-28-15:57
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String password;
    private String realName;
    private int age;
}
