package com.longfor.datav.admin.service;

import com.longfor.datav.dao.entity.TDDimensionIntegral;

import java.util.List;

/**
 * 团队排名快照服务接口
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-01
 */

public interface SnapshotTeamOverallStandingsService {

    /**
     * 快照有变动的周期数据(当前)
     * @param integralList  list
     */
    void snapshotPeriodData(List<TDDimensionIntegral> integralList);

    /**
     * 快照全部数据(当年)
     * @param integralList   list
     */
    void snapshotYearData(List<TDDimensionIntegral> integralList);
}
