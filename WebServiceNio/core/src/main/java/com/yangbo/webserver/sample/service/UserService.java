package com.yangbo.webserver.sample.service;

import com.yangbo.webserver.sample.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: yangbo
 * @Date: 2022-03-28-15:58
 * @Description:
 */
public class UserService {
    //创建对象
    private static UserService instance = new UserService();

    public static UserService getInstance() {
        return instance;
    }

    //加锁的HashMap
    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, String> online = new ConcurrentHashMap<>();

    public UserService() {
        users.put("admin", new User("bo", "123456", "bo", 18));
        users.put("wife", new User("jing", "123456", "jing", 18));
    }

    public boolean login(String username, String password) {
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = users.get(username);
        if (password.equals(user.getPassword())) {
            online.put(username, "");
            return true;
        }
        return false;
    }

    public User findByUsername(String username){
        return  users.get(username);
    }

    //更新
    public void update(User user) {
        users.put(user.getUsername(),user);
    }

}
