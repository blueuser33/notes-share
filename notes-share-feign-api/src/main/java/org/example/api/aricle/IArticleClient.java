package org.example.api.aricle;

import org.example.model.article.dtos.ArticleDto;
import org.example.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("notes-share-article")
public interface IArticleClient {
    @PostMapping("/api/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto articleDto);
}
