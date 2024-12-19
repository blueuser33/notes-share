package org.example.user.feign;

import lombok.RequiredArgsConstructor;
import org.example.api.User.IUserClient;
import org.example.model.common.dtos.ResponseResult;
import org.example.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserClient implements IUserClient {
    private final UserService userService;
    @PostMapping("/api/user/name")
    public ResponseResult queryUsername(Integer id) {
        return userService.queryUsername(id);
    }
}
