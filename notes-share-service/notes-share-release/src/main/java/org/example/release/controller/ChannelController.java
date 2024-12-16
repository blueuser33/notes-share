package org.example.release.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.common.dtos.ResponseResult;
import org.example.release.service.ChannelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;
    @PostMapping("/channels")
    public ResponseResult findAll(){
        return channelService.findAll();
    }
}
