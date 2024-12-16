package org.example.release.controller;

import lombok.RequiredArgsConstructor;

import org.example.model.common.dtos.ResponseResult;
import org.example.model.release.dtos.WmMaterialDto;
import org.example.release.service.MaterialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
public class MaterialController {
    private final MaterialService materialService;
    @PostMapping("/upload_picture")
    public ResponseResult upload_picture(MultipartFile multipartFile){
        return materialService.uploadPicture(multipartFile);
    }
    @PostMapping("/list")
    public ResponseResult materialList(@RequestBody WmMaterialDto wmMaterialDto){
        return materialService.materialList(wmMaterialDto);
    }
}
