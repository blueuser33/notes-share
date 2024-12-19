package org.example.release.service.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.api.User.IUserClient;
import org.example.api.aricle.IArticleClient;
import org.example.model.article.dtos.ArticleDto;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.release.pojos.WmChannel;
import org.example.model.release.pojos.WmNews;
import org.example.release.mapper.ChannelMapper;
import org.example.release.mapper.NewsMapper;
import org.example.release.service.AutoScanService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AutoScanServiceImpl implements AutoScanService {
    private final NewsMapper newsMapper;
    private final ChannelMapper channelMapper;
    private final IUserClient iUserClient;
    private final IArticleClient iArticleClient;
    @Override
    public void autoScan(Integer id) {
        WmNews wmNews = newsMapper.selectById(id);
        if(wmNews == null){
            throw new RuntimeException("文章不存在");
        }
        if(wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())){
            Map<String,Object> map=extractTextAndImage(wmNews);
            boolean textScan = scanText(map.get("content").toString());
            if(!textScan) return ;
            boolean imageScan = scanImage(map.get("image").toString());
            if(!imageScan) return ;
            ResponseResult result = saveArticle(wmNews);
            if(!result.getCode().equals(200)){
                throw new RuntimeException("保存Article失败");
            }
            wmNews.setArticleId((Long)result.getData());
            wmNews.setStatus((short) 9);
            wmNews.setReason("审核成功");
            newsMapper.updateById(wmNews);

        }
    }

    private ResponseResult saveArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,articleDto);
        articleDto.setLayout(wmNews.getType());
        WmChannel wmChannel = channelMapper.selectById(wmNews.getChannelId());
        if(wmChannel != null){
            articleDto.setChannelName(wmChannel.getName());
        }
        articleDto.setAuthorId(wmNews.getUserId().longValue());
        articleDto.setAuthorName((String) iUserClient.queryUsername(wmNews.getUserId()).getData());
        if(wmNews.getArticleId()!=null){
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());
        ResponseResult result = iArticleClient.saveArticle(articleDto);
        return result;

    }

    private boolean scanImage(String image) {
        return true;
    }

    private boolean scanText(String content) {
        return true;
    }

    private Map<String, Object> extractTextAndImage(WmNews wmNews) {
        StringBuilder stringBuilder=new StringBuilder();
        List<String> images=new ArrayList<>();
        if(StringUtils.isNotBlank(wmNews.getContent())){
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for(Map map:maps){
                if(map.get("type").equals("text")){
                    stringBuilder.append(map.get("value"));
                }else if(map.get("type").equals("image")){
                    images.add(map.get("value").toString());
                }

            }
        }
        if(StringUtils.isNotBlank(wmNews.getImages())){
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        Map<String,Object> result=new HashMap<>();
        result.put("content",stringBuilder.toString());
        result.put("images",images);
        return result;
    }
}
