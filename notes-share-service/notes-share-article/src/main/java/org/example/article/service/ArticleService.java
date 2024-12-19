package org.example.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.article.dtos.ArticleDto;
import org.example.model.article.dtos.ArticleHomeDto;
import org.example.model.article.pojos.ApArticle;
import org.example.model.common.dtos.ResponseResult;

public interface ArticleService extends IService<ApArticle> {
    public ResponseResult load(Short type,ArticleHomeDto articleHomeDto);

    ResponseResult saveArticle(ArticleDto articleDto);
}
