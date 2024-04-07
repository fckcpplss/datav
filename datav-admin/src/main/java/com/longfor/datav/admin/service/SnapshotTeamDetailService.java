package com.longfor.datav.admin.service;

import com.longfor.datav.dao.entity.TDDimensionIntegral;

import java.util.List;

/**
 * 快照团队详情的服务接口
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-01
 */
public interface SnapshotTeamDetailService {

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
