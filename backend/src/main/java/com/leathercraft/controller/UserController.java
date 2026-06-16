package com.leathercraft.controller;

import com.leathercraft.common.Result;
import com.leathercraft.dto.CraftProfileDTO;
import com.leathercraft.dto.LoginDTO;
import com.leathercraft.entity.User;
import com.leathercraft.service.UserService;
import com.leathercraft.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WorkService workService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody LoginDTO dto) {
        try {
            User user = userService.register(dto.getUsername(), dto.getPassword(), dto.getNickname());
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping
    public Result<Void> update(@RequestBody User user, @RequestHeader(value = "userId", required = false) Long userIdHeader) {
        if (userIdHeader == null) {
            return Result.error("请先登录");
        }
        if (user.getId() == null) {
            user.setId(userIdHeader);
        }
        if (!userIdHeader.equals(user.getId())) {
            return Result.error("无权限修改他人资料");
        }
        User existUser = userService.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        if (user.getNickname() != null) {
            existUser.setNickname(user.getNickname());
        }
        if (user.getAvatar() != null) {
            existUser.setAvatar(user.getAvatar());
        }
        if (user.getBio() != null) {
            existUser.setBio(user.getBio());
        }
        userService.updateUser(existUser);
        return Result.success();
    }

    @GetMapping("/craft-profile/{userId}")
    public Result<CraftProfileDTO> getCraftProfile(@PathVariable Long userId) {
        return Result.success(workService.getUserCraftProfile(userId));
    }
}
