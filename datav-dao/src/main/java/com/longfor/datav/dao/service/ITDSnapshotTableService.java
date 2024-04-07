package com.longfor.datav.dao.service;

import com.longfor.datav.dao.entity.TDSnapshotTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 快照表 服务类
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-30
 */
public interface ITDSnapshotTableService extends IService<TDSnapshotTable> {

    /**
     * 获取最后的快照时间
     * @param period    period
     * @param year      year
     * @return          time
     */
    String getLastPeriodByPeriod(String period, String year);

    /**
     * 查询快照id
     * @param tdSnapshotTable  快照实体
     * @return                 id
     */
    Long checkTDSnapshotTable(TDSnapshotTable tdSnapshotTable);

    /**
     * 根据快照实体更新或者保存快照
     * @param tdSnapshotTable 快照实体
     */
    void saveAndUpdate(TDSnapshotTable tdSnapshotTable);

}
