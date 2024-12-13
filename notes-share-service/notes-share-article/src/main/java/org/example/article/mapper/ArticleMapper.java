package org.example.article.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.model.article.dtos.ArticleHomeDto;
import org.example.model.article.pojos.ApArticle;

import java.util.List;

public interface ArticleMapper extends BaseMapper<ApArticle> {
    public List<ApArticle> load(@Param("dto")ArticleHomeDto articleHomeDto,@Param("type") Short type);
}
