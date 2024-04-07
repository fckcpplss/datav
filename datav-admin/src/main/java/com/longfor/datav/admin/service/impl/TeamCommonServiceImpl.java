package com.longfor.datav.admin.service.impl;

import com.longfor.datav.admin.service.TeamCommonService;
import com.longfor.datav.common.vo.TDDimensionFatherSonRelationVo;
import com.longfor.datav.dao.entity.TDDimension;
import com.longfor.datav.dao.entity.TDDimensionIntegral;
import com.longfor.datav.dao.service.ITDDimensionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队公共服务类
 *
 * @author zyh
 * @date 2024-02-05
 * @since jdk 1.8
 */

@Service
@Slf4j
public class TeamCommonServiceImpl implements TeamCommonService {

    @Resource
    private ITDDimensionService dimensionService;

    @Override
    public Map<String, List<TDDimensionIntegral>> getPeriodDimensionIntegral(List<TDDimensionIntegral> integralList) {
        //1.按照周期字段进行分组
        Map<String, List<TDDimensionIntegral>> integralMap = new HashMap<>();
        for (TDDimensionIntegral tdDimensionIntegral : integralList) {
            List<TDDimensionIntegral> tdDimensionIntegrals = new ArrayList<>();
            if (integralMap.containsKey(tdDimensionIntegral.getPeriodFlag())) {
                tdDimensionIntegrals = integralMap.get(tdDimensionIntegral.getPeriodFlag());
            }
            tdDimensionIntegrals.add(tdDimensionIntegral);
            integralMap.put(tdDimensionIntegral.getPeriodFlag(), tdDimensionIntegrals);
        }
        return integralMap;
    }

    @Override
    public Map<String, List<TDDimensionIntegral>> getCodeDimensionIntegral(List<TDDimensionIntegral> integralList) {
        //1.按照团队去获取每个维度的数据
        Map<String, List<TDDimensionIntegral>> teamTDDimensionMap = new HashMap<>();
        for (TDDimensionIntegral tdDimensionIntegral : integralList) {
            List<TDDimensionIntegral> teamIntegralList = new ArrayList<>();
            if (teamTDDimensionMap.containsKey(tdDimensionIntegral.getCodeFlag())) {
                teamIntegralList = teamTDDimensionMap.get(tdDimensionIntegral.getCodeFlag());
            }
            teamIntegralList.add(tdDimensionIntegral);
            teamTDDimensionMap.put(tdDimensionIntegral.getCodeFlag(), teamIntegralList);
        }
        return teamTDDimensionMap;
    }

    @Override
    public TDDimensionFatherSonRelationVo getTDDimensionByChildCode(String childCode) {
        TDDimensionFatherSonRelationVo vo = new TDDimensionFatherSonRelationVo();
        //1.查询所有团队指标对应的code和实体关系
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();

        //2.获取当前指标节点
        TDDimension dimension = new TDDimension();
        if (dimensionMap.containsKey(childCode)) {
            dimension = dimensionMap.get(childCode);
        }

        //3.根据当前节点获取父节点
        TDDimension pDimension = new TDDimension();
        if (dimensionMap.containsKey(dimension.getParentCode())) {
            pDimension = dimensionMap.get(dimension.getParentCode());
        }

        if(null == pDimension.getLevel()) {
            return null;
        }

        //4.获取第一级数据
        while (pDimension.getLevel() != 1) {
            if (dimensionMap.containsKey(pDimension.getParentCode())) {
                pDimension = dimensionMap.get(pDimension.getParentCode());
            }
            //若父节点code为空，则为根节点，结束循环
            if (StringUtils.isEmpty(pDimension.getParentCode())) {
                break;
            }
        }

        vo.setCName(dimension.getName());
        vo.setCValue(dimension.getCode());
        vo.setPName(pDimension.getName());
        vo.setPValue(pDimension.getCode());

        return vo;
    }

    /**
     * 根据指标分类计算各个大类的分数
     * @param firstLevelDimensionMap  map
     * @param integralList            list
     * @param dimensionMap            map
     * @return                        分数
     */
    @Override
    public double calTeamOverallStandings(List<TDDimensionIntegral> integralList, Map<String, TDDimension> dimensionMap
            , Map<String, TDDimension> firstLevelDimensionMap) {
        //1.按照一层父节点进行分类
        Map<String, List<TDDimensionIntegral>> dimensionIntegralMap = new HashMap<>();
        for(TDDimensionIntegral dimensionIntegral : integralList) {
            if(!dimensionMap.containsKey(dimensionIntegral.getDimensionCode())) {
                //若当前评分记录获取不到指标，则跳过
                continue;
            }
            TDDimension childDimension = dimensionMap.get(dimensionIntegral.getDimensionCode());
            List<TDDimensionIntegral> tdDimensionIntegralList = new ArrayList<>();
            if(dimensionIntegralMap.containsKey(childDimension.getParentCode())) {
                tdDimensionIntegralList = dimensionIntegralMap.get(childDimension.getParentCode());
            }
            tdDimensionIntegralList.add(dimensionIntegral);
            dimensionIntegralMap.put(childDimension.getParentCode(), tdDimensionIntegralList);
        }

        //2.遍历获取每个父指标的分值数据
        return getParentFraction(dimensionIntegralMap, firstLevelDimensionMap, dimensionMap);
    }

    /**
     * 父指标的分值数据
     * @param dimensionIntegralMap    map
     * @param firstLevelDimensionMap  map
     * @param dimensionMap            map
     * @return                map
     */
    private double getParentFraction(Map<String, List<TDDimensionIntegral>> dimensionIntegralMap
            , Map<String, TDDimension> firstLevelDimensionMap, Map<String, TDDimension> dimensionMap) {
        double total = 0.0;
        for(Map.Entry<String, List<TDDimensionIntegral>> entry : dimensionIntegralMap.entrySet()) {
            //1获取第一级父指标数据
            TDDimension parentDimension;
            if(!firstLevelDimensionMap.containsKey(entry.getKey())) {
                //校验第一级父指标是否存在，不存在则不处理当前记录
                continue;
            }
            parentDimension = firstLevelDimensionMap.get(entry.getKey());

            //2当前父指标下的处理每条指标得分
            double chileTotal = 0.0;
            for(TDDimensionIntegral tdDimensionIntegral : entry.getValue()) {
                //2.1获取子节点对应的子指标，需要获取期权重进行计算
                TDDimension childDimension;
                if(!dimensionMap.containsKey(tdDimensionIntegral.getDimensionCode())) {
                    //当前评分记录获取不到指标数据
                    continue;
                }
                childDimension = dimensionMap.get(tdDimensionIntegral.getDimensionCode());

                //2.2特殊处理满分100的，且含有加分的项的数据特殊规则(该规则必须包含上限分数)，其他情况需要根据实际指标考虑，这里针对代码管理指标。
                double fraction = tdDimensionIntegral.getFraction();
                if(parentDimension.getInitialScore() > 0) {
                    fraction = fraction - childDimension.getUpperLimit();
                }

                //2.3计算当前分数以及权重
                chileTotal = chileTotal + fraction * childDimension.getWeights();
            }

            //3计算当前父指标的分值，当所有子节点分值和小于0，则取0。否则取对应的分值
            chileTotal = parentDimension.getInitialScore() + chileTotal;
            if(chileTotal > 0) {
                total = total + chileTotal * parentDimension.getWeights();
            }
        }

        return total;
    }
}
