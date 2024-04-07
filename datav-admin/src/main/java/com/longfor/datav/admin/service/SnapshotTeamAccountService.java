package com.longfor.datav.admin.service;

/**
 * 团队成员快照服务接口
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-01
 */

public interface SnapshotTeamAccountService {

    /**
     * 团队成员按照周期快照
     */
    void snapshotPeriodData();

    /**
     * 团队成员按照年份快照
     */
    void snapshotYearData();
}
