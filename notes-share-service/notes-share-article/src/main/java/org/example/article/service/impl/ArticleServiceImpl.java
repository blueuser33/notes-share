package org.example.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.article.mapper.ArticleConfigMapper;
import org.example.article.mapper.ArticleContentMapper;
import org.example.article.mapper.ArticleMapper;
import org.example.article.service.ArticleService;
import org.example.model.article.dtos.ArticleDto;
import org.example.model.article.dtos.ArticleHomeDto;
import org.example.model.article.pojos.ApArticle;
import org.example.model.article.pojos.ApArticleConfig;
import org.example.model.article.pojos.ApArticleContent;
import org.example.model.common.constants.ArticleConstants;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ApArticle> implements ArticleService {
    private final Integer MAX_PAGE_SIZE=20;
    private final ArticleMapper articleMapper;
    private final ArticleContentMapper articleContentMapper;
    private final ArticleConfigMapper articleConfigMapper;

    public ResponseResult load(Short type, ArticleHomeDto articleHomeDto) {
        Integer size = articleHomeDto.getSize();
        if(size == null || size == 0) size = 0;
        size = Math.min(size,MAX_PAGE_SIZE);
        articleHomeDto.setSize(size);
        if(type != ArticleConstants.LOADTYPE_LOAD_MORE && type!=ArticleConstants.LOADTYPE_LOAD_NEW) type=ArticleConstants.LOADTYPE_LOAD_MORE;
        if(StringUtils.isBlank(articleHomeDto.getTag())) articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        if(articleHomeDto.getMaxBehotTime() == null) articleHomeDto.setMaxBehotTime(new Date());
        if(articleHomeDto.getMinBehotTime() == null) articleHomeDto.setMinBehotTime(new Date());
        List<ApArticle> articles = articleMapper.load(articleHomeDto , type);
        return ResponseResult.okResult(articles);
    }

    @Override
    public ResponseResult saveArticle(ArticleDto articleDto) {
        if(articleDto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto,apArticle);
        if(apArticle.getId()==null){

            save(apArticle);

            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            articleConfigMapper.insert(apArticleConfig);

            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(articleDto.getContent());
            articleContentMapper.insert(apArticleContent);
        }else{
            updateById(apArticle);
            ApArticleContent apArticleContent = articleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, apArticle.getId()));
            apArticleContent.setContent(articleDto.getContent());
            articleContentMapper.updateById(apArticleContent);
        }
        return ResponseResult.okResult(apArticle.getId());
    }
}
