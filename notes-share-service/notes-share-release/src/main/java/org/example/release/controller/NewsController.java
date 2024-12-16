package org.example.release.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.release.dtos.WmNewsDto;
import org.example.model.release.dtos.WmNewsPageReqDto;
import org.example.release.service.NewsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;
    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto wmNewsPageReqDto){
        return newsService.findAll(wmNewsPageReqDto);
    }

    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WmNewsDto wmNewsDto){
        return newsService.submit(wmNewsDto);
    }
}
