package com.longfor.datav.common.dto;

import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 待发货列表导入DTO
 * @author zhaoyl
 * @date 2021/12/1 下午7:22
 * @since 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@HeadRowHeight(value = 20)
@ColumnWidth(value = 20)
public class DataImportDTO implements IExcelModel, IExcelDataModel {
    /**
     * 行号
     */
    @ExcelIgnore
    private int rowNum;

    /**
     * 错误信息
     */
    @ExcelIgnore
    private String errorMsg;

    @Override
    public int getRowNum() {
        return this.rowNum;
    }

    @Override
    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
