package com.leathercraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leathercraft.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
    User register(String username, String password, String nickname);
    User getById(Long id);
    void updateUser(User user);
}
