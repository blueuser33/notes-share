package org.example.release.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.release.pojos.WmChannel;
import org.example.release.mapper.ChannelMapper;
import org.example.release.service.ChannelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, WmChannel> implements ChannelService {
    @Override
    public ResponseResult findAll() {

        return ResponseResult.okResult(list());
    }
}
