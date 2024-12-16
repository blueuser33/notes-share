package org.example.release.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.release.dtos.WmMaterialDto;
import org.example.model.release.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.Response;

public interface MaterialService extends IService<WmMaterial> {
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    ResponseResult materialList(WmMaterialDto wmMaterialDto);
}
