package org.example.article.feign;

import lombok.RequiredArgsConstructor;
import org.example.api.aricle.IArticleClient;
import org.example.article.service.ArticleService;
import org.example.model.article.dtos.ArticleDto;
import org.example.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleClient implements IArticleClient {
    private final ArticleService articleService;
    @PostMapping("/api/article/save")
    public ResponseResult saveArticle(ArticleDto articleDto) {
        return articleService.saveArticle(articleDto);
    }
}
