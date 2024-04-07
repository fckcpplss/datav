package com.longfor.datav.admin.service;

import com.longfor.datav.common.vo.req.CalculateScoreReq;
import com.longfor.datav.common.vo.resp.CalculateScoreResp;
import org.springframework.web.multipart.MultipartFile;

/**
 * 数据管理接口类
 */
public interface IDataService {

    String dataImport(MultipartFile file);

    /**
     * 处理个人快照数据入库
     * @param account
     * @param month
     */
    void handelPersonSnapshotData(String account, String year,String month,String sprint);

    /**
     * 处理个人快照数据入库
     * @param account
     * @param month
     */
    void handelPersonSnapshotData();

    /**
     * 规则计算得分
     * @param req
     * @return
     */
    CalculateScoreResp calculateScore(CalculateScoreReq req);
}
