package org.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.user.dtos.LoginDto;
import org.example.model.user.pojos.ApUser;

public interface UserService extends IService<ApUser> {
    public ResponseResult login(LoginDto loginDto);
}
