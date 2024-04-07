package com.longfor.datav.admin.service;

import com.longfor.datav.common.vo.TDDimensionFatherSonRelationVo;
import com.longfor.datav.dao.entity.TDDimension;
import com.longfor.datav.dao.entity.TDDimensionIntegral;

import java.util.List;
import java.util.Map;

/**
 * 团队公共的服务接口
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-05
 */
public interface TeamCommonService {

    /**
     * 获取每个周期对应的指标列表
     * @param integralList  list
     * @return              map
     */
    Map<String, List<TDDimensionIntegral>> getPeriodDimensionIntegral(List<TDDimensionIntegral> integralList);

    /**
     * 获取每个团队对应的指标列表
     * @param integralList  list
     * @return              map
     */
    Map<String, List<TDDimensionIntegral>> getCodeDimensionIntegral(List<TDDimensionIntegral> integralList);

    /**
     * 根据子编码查询父信息
     * @param childCode  childCode
     * @return           vo
     */
    TDDimensionFatherSonRelationVo getTDDimensionByChildCode(String childCode);

    /**
     * 计算总积分
     * @param integralList             积分记录
     * @param dimensionMap             指标map
     * @param firstLevelDimensionMap   第一级积分层级
     * @return                         总分
     */
    double calTeamOverallStandings(List<TDDimensionIntegral> integralList, Map<String, TDDimension> dimensionMap
            , Map<String, TDDimension> firstLevelDimensionMap);

}
