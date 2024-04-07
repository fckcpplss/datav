package com.longfor.datav.admin.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.datav.admin.service.IDataService;
import com.longfor.datav.common.enums.DeleteStatusEnum;
import com.longfor.datav.dao.entity.TDAccount;
import com.longfor.datav.dao.service.ITDAccountService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 个人得分数据统计到快照
 * @author zhaoyl
 * @date 2024/2/1 10:49
 * @since 1.0
 */

@JobHandler("PersonScoreStatistics")
@Component
public class PersonScoreStatistics extends IJobHandler {

    @Autowired
    private ITDAccountService accountService;

    @Autowired
    private IDataService dataService;



    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("同步个人得分信息开始,param:{}",param);
        //查询账号表数据
        List<TDAccount> accountList = accountService.list(Wrappers.<TDAccount>lambdaQuery().eq(TDAccount::getIsDelete, DeleteStatusEnum.NO.getCode()));
        XxlJobLogger.log("同步个人得分信息,查询人员数据条数:{}",accountList.size());
        if(CollectionUtils.isEmpty(accountList)){
            return null;
        }
        accountList.stream().forEach(item -> {
            dataService.handelPersonSnapshotData(item.getAccount(),null,null,null);
        });
        return ReturnT.SUCCESS;
    }
}