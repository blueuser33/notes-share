package org.example.release.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.avro.data.Json;
import org.apache.commons.lang.StringUtils;
import org.example.common.constants.WemediaConstants;
import org.example.common.exception.CustomException;
import org.example.model.common.dtos.PageResponseResult;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.common.enums.AppHttpCodeEnum;
import org.example.model.release.dtos.WmMaterialDto;
import org.example.model.release.dtos.WmNewsDto;
import org.example.model.release.dtos.WmNewsPageReqDto;
import org.example.model.release.pojos.WmMaterial;
import org.example.model.release.pojos.WmNews;
import org.example.model.release.pojos.WmNewsMaterial;
import org.example.model.user.pojos.ApUser;
import org.example.release.mapper.MaterialMapper;
import org.example.release.mapper.NewsMapper;
import org.example.release.mapper.NewsMaterialRelationMapper;
import org.example.release.service.NewsService;
import org.example.utils.thread.UserThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl extends ServiceImpl<NewsMapper, WmNews> implements NewsService {
    private final NewsMaterialRelationMapper newsMaterialRelationMapper;
    private final MaterialMapper materialMapper;
    @Override
    public ResponseResult findAll(WmNewsPageReqDto wmNewsPageReqDto) {
        if(wmNewsPageReqDto == null ) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        wmNewsPageReqDto.checkParam();
        LambdaQueryWrapper<WmNews> wmNewsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(wmNewsPageReqDto.getStatus()!=null){
            wmNewsLambdaQueryWrapper.eq(WmNews::getStatus,wmNewsPageReqDto.getStatus());
        }
        if(wmNewsPageReqDto.getChannelId() != null){
            wmNewsLambdaQueryWrapper.eq(WmNews::getChannelId,wmNewsPageReqDto.getChannelId());
        }
        if(StringUtils.isNotBlank(wmNewsPageReqDto.getKeyword())){
            wmNewsLambdaQueryWrapper.like(WmNews::getTitle,wmNewsPageReqDto.getKeyword());
        }
        if(wmNewsPageReqDto.getBeginPubDate()!=null && wmNewsPageReqDto.getEndPubDate()!=null){
            wmNewsLambdaQueryWrapper.between(WmNews::getCreatedTime,wmNewsPageReqDto.getBeginPubDate(),wmNewsPageReqDto
                    .getEndPubDate());
        }
        ApUser user = UserThreadLocalUtil.getUser();
        if(user!=null){
            wmNewsLambdaQueryWrapper.eq(WmNews::getUserId,user.getId());
        }
        IPage<WmNews> page = new Page<WmNews>(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize());
        IPage<WmNews> result = this.page(page, wmNewsLambdaQueryWrapper);
        PageResponseResult pageResponseResult = new PageResponseResult(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize(), (int) result.getTotal());
        pageResponseResult.setData(result.getRecords());
        return pageResponseResult;
    }

    @Override
    public ResponseResult submit(WmNewsDto wmNewsDto) {
        if(wmNewsDto == null || wmNewsDto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(wmNewsDto,wmNews);
        if(wmNewsDto.getImages()!=null && wmNewsDto.getImages().size() > 0){
            String imageStr=StringUtils.join(wmNewsDto.getImages(),",");
            wmNews.setImages(imageStr);
        }
        if(wmNewsDto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }
        saveOrUpdateWmNews(wmNews);
        if(wmNewsDto.getStatus()!=null && wmNewsDto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        List<String> imageUrls = extractImg(wmNews.getContent());
        saveContentRelations(imageUrls,wmNews);
        saveCoverRelations(wmNewsDto,imageUrls,wmNews);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private void saveCoverRelations(WmNewsDto wmNewsDto,List<String> imgUrls,WmNews wmNews) {
        List<String> images = wmNewsDto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if(wmNewsDto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            //多图
            if(imgUrls.size() >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = imgUrls.stream().limit(3).collect(Collectors.toList());
            }else if(imgUrls.size() >= 1 && imgUrls.size() < 3){
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = imgUrls.stream().limit(1).collect(Collectors.toList());
            }else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if(images != null && images.size() > 0){
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        if(images!=null && !images.isEmpty()){
            //通过图片的url查询素材的id
            List<WmMaterial> dbMaterials = materialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, images));

            //判断素材是否有效
            if(dbMaterials==null || dbMaterials.size() == 0){
                //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            if(images.size() != dbMaterials.size()){
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            //批量保存
            newsMaterialRelationMapper.saveRelation(idList, wmNews.getId(), WemediaConstants.WM_CONTENT_REFERENCE);
        }
    }

    private void saveContentRelations(List<String> imageUrls,WmNews wmNews) {
        if(imageUrls!=null && !imageUrls.isEmpty()){
            //通过图片的url查询素材的id
            List<WmMaterial> dbMaterials = materialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, imageUrls));

            //判断素材是否有效
            if(dbMaterials==null || dbMaterials.size() == 0){
                //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            if(imageUrls.size() != dbMaterials.size()){
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            //批量保存
            newsMaterialRelationMapper.saveRelation(idList, wmNews.getId(), WemediaConstants.WM_CONTENT_REFERENCE);
        }

    }

    private List<String> extractImg(String content) {
        List<String> materials=new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for(Map map:maps){
            if(map.get("type").equals("image")){
                materials.add(map.get("value").toString());
            }
        }
        return materials;

    }

    private void saveOrUpdateWmNews(WmNews wmNews) {
        wmNews.setUserId(UserThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short)1);
        if(wmNews.getId() == null){
            save(wmNews);
        }else{
            newsMaterialRelationMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            updateById(wmNews);
        }
    }
}
