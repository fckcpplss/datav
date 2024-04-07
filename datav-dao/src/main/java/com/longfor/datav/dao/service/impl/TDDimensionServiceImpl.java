package com.longfor.datav.dao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.dao.entity.TDDimension;
import com.longfor.datav.dao.mapper.TDDimensionMapper;
import com.longfor.datav.dao.service.ITDDimensionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 维度 服务实现类
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Service
public class TDDimensionServiceImpl extends ServiceImpl<TDDimensionMapper, TDDimension> implements ITDDimensionService {

    /**
     * 获取团队指标名称和code对应map
     *
     * @return map
     */
    @Override
    public Map<String, TDDimension> getDimensionCodeNameMap() {
        return getDimensionCodeNameMapByLevel(0);
    }

    /**
     * 根据层级和类型获取指标数据
     *
     * @param level 层级
     * @return list
     */
    @Override
    public List<TDDimension> getDimensionByLevel(int level, int type) {
        LambdaQueryWrapper<TDDimension> dimensionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dimensionLambdaQueryWrapper.eq(TDDimension::getLevel, level).eq(TDDimension::getType, 1);
        return this.list(dimensionLambdaQueryWrapper);
    }

    /**
     * 获取所有团队一层指标的code与实体的map关系
     * @return map
     */
    @Override
    public Map<String, TDDimension> getFirstDimensionCodeEntry() {
        return getDimensionCodeNameMapByLevel(1);
    }

    /**
     * 获取子code与父code的数据关系
     * @return  map
     */
    @Override
    public Map<String, String> getChildCodeWithParentCode() {
        LambdaQueryWrapper<TDDimension> dimensionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dimensionLambdaQueryWrapper.eq(TDDimension::getType, 1)
                .eq(TDDimension::getLevel, 2)
                .eq(TDDimension::getIsDelete, 0)
                .eq(TDDimension::getStatus, 1);
        List<TDDimension> dimensionList = this.list(dimensionLambdaQueryWrapper);

        Map<String, String> childCodeWithParent = new HashMap<>();
        for(TDDimension dimension : dimensionList) {
            childCodeWithParent.put(dimension.getCode(), dimension.getParentCode());
        }

        return childCodeWithParent;
    }

    /**
     * 根据level获取code与实体的map
     * @param level  等级
     * @return       map
     */
    private Map<String, TDDimension> getDimensionCodeNameMapByLevel(int level) {
        LambdaQueryWrapper<TDDimension> dimensionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dimensionLambdaQueryWrapper.eq(TDDimension::getType, 1)
                .eq(TDDimension::getIsDelete, 0)
                .eq(TDDimension::getStatus, 1);
        if(level > 0) {
            dimensionLambdaQueryWrapper.eq(TDDimension::getLevel, level);
        }

        List<TDDimension> dimensionList = this.list(dimensionLambdaQueryWrapper);

        Map<String, TDDimension> codeNameMap = new HashMap<>();
        for (TDDimension dimension : dimensionList) {
            codeNameMap.put(dimension.getCode(), dimension);
        }
        return codeNameMap;
    }
}
