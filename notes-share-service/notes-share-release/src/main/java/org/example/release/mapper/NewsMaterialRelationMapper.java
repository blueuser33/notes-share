package org.example.release.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.model.release.pojos.WmNewsMaterial;

import java.util.List;

public interface NewsMaterialRelationMapper extends BaseMapper<WmNewsMaterial> {
    void saveRelation(@Param("materialIds")List<Integer> materialIds,@Param("newsId")Integer newsId,@Param("type")Short type);
}
