package org.example.release.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.file.service.FileStorageService;
import org.example.model.common.dtos.PageRequestDto;
import org.example.model.common.dtos.PageResponseResult;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.common.enums.AppHttpCodeEnum;
import org.example.model.release.dtos.WmMaterialDto;
import org.example.model.release.pojos.WmMaterial;
import org.example.release.mapper.MaterialMapper;
import org.example.release.service.MaterialService;
import org.example.utils.thread.UserThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, WmMaterial> implements MaterialService {
    private final FileStorageService fileStorageService;
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        if(multipartFile == null || multipartFile.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String filename= UUID.randomUUID().toString().replace("-","");
        String originalFilename = multipartFile.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId=null;
        try {
            fileId = fileStorageService.uploadImgFile("", filename + substring, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}",fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(UserThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        return ResponseResult.okResult(wmMaterial);

    }

    @Override
    public ResponseResult materialList(WmMaterialDto wmMaterialDto) {
        if(wmMaterialDto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        wmMaterialDto.checkParam();
        IPage<WmMaterial> page = new Page(wmMaterialDto.getPage(), wmMaterialDto.getSize());
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(wmMaterialDto.getIsCollection()!=null && wmMaterialDto.getIsCollection()==1){
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection,wmMaterialDto.getIsCollection());
        }
        lambdaQueryWrapper.eq(WmMaterial::getUserId,UserThreadLocalUtil.getUser().getId());
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        IPage<WmMaterial> page1 = this.page(page, lambdaQueryWrapper);
        PageResponseResult pageResponseResult = new PageResponseResult(wmMaterialDto.getPage(), wmMaterialDto.getSize(), (int) page1.getTotal());
        pageResponseResult.setData(page1.getRecords());

        return pageResponseResult;
    }
}
