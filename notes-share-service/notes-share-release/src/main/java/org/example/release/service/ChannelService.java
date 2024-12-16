package org.example.release.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.release.pojos.WmChannel;

public interface ChannelService extends IService<WmChannel> {
    public ResponseResult findAll();
}
