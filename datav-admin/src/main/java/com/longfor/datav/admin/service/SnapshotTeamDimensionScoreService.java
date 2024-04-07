package com.longfor.datav.admin.service;

import com.longfor.datav.dao.entity.TDDimensionIntegral;
import java.util.List;

/**
 * 快照每个指标的团队评分
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-01
 */

public interface SnapshotTeamDimensionScoreService {

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
