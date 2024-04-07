package com.longfor.datav.dao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.dao.entity.TDSnapshotTable;
import com.longfor.datav.dao.entity.TDTimeSprintRelation;
import com.longfor.datav.dao.mapper.TDSnapshotTableMapper;
import com.longfor.datav.dao.service.ITDSnapshotTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfor.datav.dao.service.ITDTimeSprintRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 快照表 服务实现类
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-30
 */
@Service
public class TDSnapshotTableServiceImpl extends ServiceImpl<TDSnapshotTableMapper, TDSnapshotTable> implements ITDSnapshotTableService {

    @Resource
    private ITDTimeSprintRelationService itdTimeSprintRelationService;

    /**
     * 通过周期编码获取改编码的上一个周期的周期编码
     *
     * @param period period
     * @return str
     */
    @Override
    public String getLastPeriodByPeriod(String period, String year) {
        //1.获取当前周期的时间
        LambdaQueryWrapper<TDTimeSprintRelation> relationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        relationLambdaQueryWrapper.eq(TDTimeSprintRelation::getPeriod, period)
                .eq(TDTimeSprintRelation::getYear, year);

        TDTimeSprintRelation relation = itdTimeSprintRelationService.getOne(relationLambdaQueryWrapper);
        if (ObjectUtils.isEmpty(relation)) {
            return null;
        }

        //2.获取上一个周期的周期编码
        LambdaQueryWrapper<TDTimeSprintRelation> relationLambdaQueryWrapperNew = new LambdaQueryWrapper<>();
        relationLambdaQueryWrapperNew.eq(TDTimeSprintRelation::getYear, year)
                .lt(TDTimeSprintRelation::getEndTime, relation.getStartTime())
                .orderByDesc(TDTimeSprintRelation::getEndTime);
        List<TDTimeSprintRelation> relationList = itdTimeSprintRelationService.list(relationLambdaQueryWrapperNew);
        if (CollectionUtils.isEmpty(relationList)) {
            return null;
        }

        return relationList.get(0).getPeriod();
    }

    /**
     * 校验当前数据是否存在，存在则返回id
     *
     * @param tdSnapshotTable table
     * @return id
     */
    @Override
    public Long checkTDSnapshotTable(TDSnapshotTable tdSnapshotTable) {
        LambdaQueryWrapper<TDSnapshotTable> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TDSnapshotTable::getType, tdSnapshotTable.getType())
                .eq(TDSnapshotTable::getPeriodFlag, tdSnapshotTable.getPeriodFlag())
                .eq(TDSnapshotTable::getCodeFlag, tdSnapshotTable.getCodeFlag())
                .eq(TDSnapshotTable::getYear, tdSnapshotTable.getYear())
                .eq(TDSnapshotTable::getModelFlag, tdSnapshotTable.getModelFlag());
        TDSnapshotTable tdSnapshotTable1 = this.getOne(lambdaQueryWrapper);
        if (ObjectUtils.isEmpty(tdSnapshotTable1)) {
            return null;
        }
        return tdSnapshotTable1.getId();
    }

    /**
     * 根据快照实体更新或者保存快照
     * @param tdSnapshotTable 快照实体
     */
    @Override
    public void saveAndUpdate(TDSnapshotTable tdSnapshotTable) {
        //1.校验是否存在数据
        Long id = this.checkTDSnapshotTable(tdSnapshotTable);
        if (null != id) {
            tdSnapshotTable.setId(id);
        }

        //2.保存当前数据
        this.saveOrUpdate(tdSnapshotTable);
    }
}
