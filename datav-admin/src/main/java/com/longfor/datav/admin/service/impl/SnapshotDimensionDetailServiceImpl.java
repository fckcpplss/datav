package com.longfor.datav.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.admin.service.SnapshotDimensionDetailService;
import com.longfor.datav.admin.service.TeamCommonService;
import com.longfor.datav.common.enums.DimensionTypeEnum;
import com.longfor.datav.common.enums.SnapshotAccountTypeEnum;
import com.longfor.datav.common.enums.TeamModelDataEnum;
import com.longfor.datav.common.utils.TimeUtil;
import com.longfor.datav.common.vo.TDDimensionFatherSonRelationVo;
import com.longfor.datav.common.vo.TeamChildDimensionVo;
import com.longfor.datav.common.vo.resp.TeamDimensionDetailResponse;
import com.longfor.datav.dao.entity.TDDimension;
import com.longfor.datav.dao.entity.TDDimensionIntegral;
import com.longfor.datav.dao.entity.TDSnapshotTable;
import com.longfor.datav.dao.entity.TDTeam;
import com.longfor.datav.dao.service.ITDDimensionService;
import com.longfor.datav.dao.service.ITDSnapshotTableService;
import com.longfor.datav.dao.service.ITDTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快照团队指标得分明细服务实现
 *
 * @author zyh
 * @date 2024-02-01
 * @since jdk 1.8
 */

@Service
@Slf4j
public class SnapshotDimensionDetailServiceImpl implements SnapshotDimensionDetailService {

    @Resource
    private ITDSnapshotTableService tableService;

    @Resource
    private ITDTeamService teamService;

    @Resource
    private ITDDimensionService dimensionService;

    @Resource
    private TeamCommonService commonService;

    @Override
    public void snapshotPeriodData(List<TDDimensionIntegral> integralList) {
        //1.按照周期字段进行分组
        Map<String, List<TDDimensionIntegral>> integralMap = commonService.getPeriodDimensionIntegral(integralList);

        //2.1.获取指标对应名称编码
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();
        //2.2.获取团队第一级指标数据，需要全部展示
        List<TDDimension> dimensionList = dimensionService.getDimensionByLevel(1, DimensionTypeEnum.TEAM.getCode());

        //2.按照周期处理团队的各个指标
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : integralMap.entrySet()) {
            dealTeamDimensionScore(entry.getKey(), entry.getValue(), dimensionMap, dimensionList);
        }
    }

    @Override
    public void snapshotYearData(List<TDDimensionIntegral> integralList) {
        //1.获取指标对应名称编码
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();
        //2.获取团队第一级指标数据，需要全部展示
        List<TDDimension> dimensionList = dimensionService.getDimensionByLevel(1, DimensionTypeEnum.TEAM.getCode());
        //3.按团队统计每个指标全年的得分
        dealTeamDimensionScore(null, integralList, dimensionMap, dimensionList);
    }

    /**
     * 处理团队得分情况
     *
     * @param period       周期
     * @param integralList list
     */
    private void dealTeamDimensionScore(String period, List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> dimensionMap, List<TDDimension> dimensionList) {
        //1根据团队获取得分数据
        Map<String, List<TDDimensionIntegral>> teamMap = commonService.getCodeDimensionIntegral(integralList);

        //2遍历团队，保存指标详细得分
        for (Map.Entry<String, List<TDDimensionIntegral>> listEntry : teamMap.entrySet()) {
            //2.1获取团队信息
            LambdaQueryWrapper<TDTeam> tdTeamLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tdTeamLambdaQueryWrapper.eq(TDTeam::getCode, listEntry.getKey());
            TDTeam team = teamService.getOne(tdTeamLambdaQueryWrapper);
            if (ObjectUtils.isEmpty(team)) {
                continue;
            }

            //2.2获取各项指标得分明细
            List<TeamDimensionDetailResponse> responseList = getTeamDimensionDetail(listEntry.getValue()
                    , dimensionMap, dimensionList);

            //2.3保存数据
            TDSnapshotTable snapshotTable = new TDSnapshotTable();
            snapshotTable.setType(SnapshotAccountTypeEnum.TEAM.getCode());
            snapshotTable.setCodeFlag(listEntry.getKey());
            snapshotTable.setNameFlag(team.getName());
            if (StringUtils.isEmpty(period)) {
                snapshotTable.setPeriodFlag(TimeUtil.getCurrentYear());
            } else {
                snapshotTable.setPeriodFlag(period);
            }
            snapshotTable.setSnapshotTime(LocalDateTime.now());
            snapshotTable.setContent(JSON.toJSONString(responseList));

            snapshotTable.setYear(TimeUtil.getCurrentYear());
            snapshotTable.setModelFlag(TeamModelDataEnum.TEAM_DIMENSION_DETAIL.getCode());

            tableService.saveAndUpdate(snapshotTable);
        }
    }

    /**
     * 根据积分数据获取各个指标的得分明细
     *
     * @param integralList list
     * @return response
     */
    private List<TeamDimensionDetailResponse> getTeamDimensionDetail(List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> dimensionMap, List<TDDimension> dimensionList) {
        //1.获取指标数据，以及每个二层指标包含的三层指标的积分关系
        Map<String, List<TeamChildDimensionVo>> dimensionListMap = new HashMap<>();

        //2.初始化指标map
        for (TDDimension dimension : dimensionList) {
            dimensionListMap.put(dimension.getCode(), new ArrayList<>());
        }

        //3.处理三层与二层指标的映射关系
        for (TDDimensionIntegral integral : integralList) {
            TDDimensionFatherSonRelationVo vo = commonService.getTDDimensionByChildCode(integral.getDimensionCode());
            if (ObjectUtils.isEmpty(vo)) {
                continue;
            }
            if (dimensionListMap.containsKey(vo.getPValue())) {
                List<TeamChildDimensionVo> teamChildDimensionVos = dimensionListMap.get(vo.getPValue());
                TeamChildDimensionVo teamChildDimensionVo = new TeamChildDimensionVo();
                teamChildDimensionVo.setChildDimensionCode(integral.getDimensionCode());
                if (dimensionMap.containsKey(integral.getDimensionCode())) {
                    teamChildDimensionVo.setChildDimensionName(dimensionMap.get(integral.getDimensionCode()).getName());
                    teamChildDimensionVo.setDesc(dimensionMap.get(integral.getDimensionCode()).getRemark());
                }
                teamChildDimensionVo.setScore(integral.getFraction());
                teamChildDimensionVo.setValue(integral.getValue());
                teamChildDimensionVos.add(teamChildDimensionVo);
                dimensionListMap.put(vo.getPValue(), teamChildDimensionVos);
            }
        }

        //4.处理返回实体
        List<TeamDimensionDetailResponse> teamDimensionDetailResponses = new ArrayList<>();
        for (Map.Entry<String, List<TeamChildDimensionVo>> entry : dimensionListMap.entrySet()) {
            TeamDimensionDetailResponse response = new TeamDimensionDetailResponse();
            response.setDimensionCode(entry.getKey());
            if (dimensionMap.containsKey(entry.getKey())) {
                response.setDimensionName(dimensionMap.get(entry.getKey()).getName());
                response.setDesc(dimensionMap.get(entry.getKey()).getRemark());
            }
            response.setChildList(entry.getValue());
            teamDimensionDetailResponses.add(response);
        }

        return teamDimensionDetailResponses;
    }
}
