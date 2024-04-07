package com.longfor.datav.dao.service;

import com.longfor.datav.dao.entity.TDDimension;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 维度 服务类
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
public interface ITDDimensionService extends IService<TDDimension> {

    Map<String, TDDimension> getDimensionCodeNameMap();

    List<TDDimension> getDimensionByLevel(int level, int type);

    Map<String, TDDimension> getFirstDimensionCodeEntry();

    Map<String, String> getChildCodeWithParentCode();

}
