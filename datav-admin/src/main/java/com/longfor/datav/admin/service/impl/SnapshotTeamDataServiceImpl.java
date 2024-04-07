package com.longfor.datav.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.admin.service.*;
import com.longfor.datav.common.enums.SnapshotAccountTypeEnum;
import com.longfor.datav.common.utils.TimeUtil;
import com.longfor.datav.dao.entity.TDDimensionIntegral;
import com.longfor.datav.dao.entity.TDSnapshotTable;
import com.longfor.datav.dao.service.ITDDimensionIntegralService;
import com.longfor.datav.dao.service.ITDSnapshotTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 快照团队数据服务实现类
 *
 * @author zyh
 * @date 2024-01-31
 * @since jdk 1.8
 */

@Service
@Slf4j
public class SnapshotTeamDataServiceImpl implements SnapshotTeamDataService {

    @Resource
    private ITDSnapshotTableService itdSnapshotTableService;

    @Resource
    private ITDDimensionIntegralService integralService;

    @Resource
    private SnapshotTeamDetailService detailService;

    @Resource
    private SnapshotDimensionDetailService dimensionDetailService;

    @Resource
    private SnapshotTeamAccountService accountService;

    @Resource
    private SnapshotTeamDimensionScoreService scoreService;

    @Resource
    private SnapshotTeamHistoryService historyService;

    @Resource
    private SnapshotTeamOverallStandingsService standingsService;

    @Override
    public void snapshotTeam() {
        //1.先获取上次快照的时间点
        LambdaQueryWrapper<TDSnapshotTable> tableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tableLambdaQueryWrapper.select(TDSnapshotTable::getSnapshotTime)
                .eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDSnapshotTable::getYear, TimeUtil.getCurrentYear())
                .orderByDesc(TDSnapshotTable::getSnapshotTime).last("LIMIT 1");

        TDSnapshotTable tdSnapshotTable = itdSnapshotTableService.getOne(tableLambdaQueryWrapper);

        //2.校验之前是否已经快照，若有快照数据，则此次快照为快照最插入的数据
        if (!ObjectUtils.isEmpty(tdSnapshotTable)) {
            snapshotPeriodTeam(tdSnapshotTable.getSnapshotTime());
        } else {
            snapshotPeriodTeamByCurrentYear();
        }

        //4.快照全部数据，按照年份累加快照
        snapshotCurrentYearTeam();
    }

    /**
     * 根据上次快照时间，快照此次更新的周期数据
     *
     * @param lastSnapshotTime 上次快照时间
     */
    private void snapshotPeriodTeam(LocalDateTime lastSnapshotTime) {
        //1.根据上次快照时间，扫描积分表有变动的数据
        LambdaQueryWrapper<TDDimensionIntegral> integralLambdaQueryWrapper = new LambdaQueryWrapper<>();
        integralLambdaQueryWrapper.select(TDDimensionIntegral::getPeriodFlag)
                .eq(TDDimensionIntegral::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .ge(TDDimensionIntegral::getCreateTime, lastSnapshotTime).or()
                .ge(TDDimensionIntegral::getUpdateTime, lastSnapshotTime);

        List<Object> periodList = integralService.listObjs(integralLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(periodList)) {
            log.info("[snapshotTeam]--按照上次快照时间获取积分表数据为空，上次快照时间:{}", lastSnapshotTime);
            return;
        }

        //2.获取有变动的所有周期数据
        List<Object> objectList = periodList.stream().distinct().collect(Collectors.toList());
        LambdaQueryWrapper<TDDimensionIntegral> integralLambdaQueryWrapperNew = new LambdaQueryWrapper<>();
        integralLambdaQueryWrapperNew.eq(TDDimensionIntegral::getYear, TimeUtil.getCurrentYear())
                .in(TDDimensionIntegral::getPeriodFlag, objectList);

        List<TDDimensionIntegral> integralList = integralService.list(integralLambdaQueryWrapperNew);
        if (CollectionUtils.isEmpty(integralList)) {
            log.info("[snapshotTeam]--按照上次快照时间获取积分表周期数据，再统一获取周期以及当年的全部变动数据为空" +
                    "，变动周期数据：{}, 当前时间：{}", objectList, TimeUtil.getCurrentYear());
            return;
        }

        //3.统一处理快照数据
        dealPeriodSnapshotData(integralList);
    }

    /**
     * 按照当前年份快照数据(若无快照的情况下)
     */
    private void snapshotPeriodTeamByCurrentYear() {
        //1.根据年份获取所有积分数据
        LambdaQueryWrapper<TDDimensionIntegral> integralLambdaQueryWrapper = new LambdaQueryWrapper<>();
        integralLambdaQueryWrapper.eq(TDDimensionIntegral::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDDimensionIntegral::getYear, TimeUtil.getCurrentYear());

        List<TDDimensionIntegral> integralList = integralService.list(integralLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(integralList)) {
            log.info("[snapshotTeam]--无快照的情况下查询当前年份的积分数据为空，当前年份：{}", TimeUtil.getCurrentFirstTimeByYear());
            return;
        }

        //2.处理快照数据
        dealPeriodSnapshotData(integralList);
    }

    /**
     * 统一处理快照数据
     *
     * @param integralList list
     */
    private void dealPeriodSnapshotData(List<TDDimensionIntegral> integralList) {
        standingsService.snapshotPeriodData(integralList);
        accountService.snapshotPeriodData();
        dimensionDetailService.snapshotPeriodData(integralList);
        detailService.snapshotPeriodData(integralList);
        scoreService.snapshotPeriodData(integralList);
        historyService.snapshotPeriodData(integralList);
    }

    /**
     * 快照当年的全部数据
     */
    private void snapshotCurrentYearTeam() {
        //1.根据年份获取所有积分数据
        LambdaQueryWrapper<TDDimensionIntegral> integralLambdaQueryWrapper = new LambdaQueryWrapper<>();
        integralLambdaQueryWrapper.eq(TDDimensionIntegral::getYear, TimeUtil.getCurrentYear())
                .eq(TDDimensionIntegral::getType, SnapshotAccountTypeEnum.TEAM.getCode());

        List<TDDimensionIntegral> integralList = integralService.list(integralLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(integralList)) {
            log.info("[snapshotTeam]--快照全部数据，当前年份：{}", TimeUtil.getCurrentFirstTimeByYear());
            return;
        }

        //2.按照模块进行快照
        standingsService.snapshotYearData(integralList);
        accountService.snapshotYearData();
        dimensionDetailService.snapshotYearData(integralList);
        detailService.snapshotYearData(integralList);
        scoreService.snapshotYearData(integralList);
        historyService.snapshotYearData(integralList);
    }
}
