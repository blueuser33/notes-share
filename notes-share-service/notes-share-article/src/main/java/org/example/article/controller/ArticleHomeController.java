package org.example.article.controller;

import lombok.RequiredArgsConstructor;
import org.example.article.service.ArticleService;
import org.example.model.article.dtos.ArticleHomeDto;
import org.example.model.common.constants.ArticleConstants;
import org.example.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleHomeController {
    private final ArticleService articleService;
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto articleHomeDto){
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,articleHomeDto);
    }
    @PostMapping("/loadMore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto articleHomeDto){
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,articleHomeDto);
    }
    @PostMapping("/loadNew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto articleHomeDto){
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_NEW,articleHomeDto);
    }
}
