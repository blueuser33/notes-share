package org.example.user.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.common.enums.AppHttpCodeEnum;
import org.example.model.user.dtos.LoginDto;
import org.example.model.user.pojos.ApUser;
import org.example.user.mapper.UserMapper;
import org.example.user.service.UserService;
import org.example.utils.common.AppJwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, ApUser> implements UserService  {
    public ResponseResult login(LoginDto loginDto) {
        if(StringUtils.isBlank(loginDto.getPhone())||StringUtils.isBlank(loginDto.getPassword())){
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0l));
            return ResponseResult.okResult(map);
        }
        ApUser user = lambdaQuery().eq(ApUser::getPhone, loginDto.getPhone()).one();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");

        }
        String salt = user.getSalt();
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        Map<String,Object> map=new HashMap<>();
        map.put("token",AppJwtUtil.getToken(user.getId().longValue()));
        user.setSalt("");
        user.setPassword("");
        map.put("user",user);
        return ResponseResult.okResult(map);

    }

    @Override
    public ResponseResult queryUsername(Integer id) {
        ApUser user = lambdaQuery().eq(ApUser::getId, id).one();

        return ResponseResult.okResult(user.getName());
    }
}
