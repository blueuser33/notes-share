package org.example.api.User;

import org.example.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("notes-share-user")
public interface IUserClient {
    @PostMapping("/api/user/name")
    public ResponseResult queryUsername(Integer id);
}
