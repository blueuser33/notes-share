package org.example.model.article.dtos;

import lombok.Data;
import org.example.model.article.pojos.ApArticle;
@Data
public class ArticleDto extends ApArticle {
    private String  content;
}
