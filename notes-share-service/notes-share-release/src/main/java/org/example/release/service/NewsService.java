package org.example.release.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.release.dtos.WmNewsDto;
import org.example.model.release.dtos.WmNewsPageReqDto;
import org.example.model.release.pojos.WmNews;

public interface NewsService extends IService<WmNews> {
    public ResponseResult findAll(WmNewsPageReqDto wmNewsPageReqDto);

    ResponseResult submit(WmNewsDto wmNewsDto);
}
