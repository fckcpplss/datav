package com.longfor.datav.common.vo;


import com.longfor.datav.common.enums.CommonEnum;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;

/**
 * 统一分页返回封装
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
public class PageResponse<D> extends Response<D> {
    private static final long serialVersionUID = 1L;
    /**
     * 分页信息
     */
    private PageInfo pageInfo;
    /**
     * 总条数
     */
    private Long total;
    /**
     * 当前页码
     */
    private long page;

    /**
     * 页码大小
     */
    private Integer size;

    public PageResponse(D data, Long totalCount, IResponseEnum responseEnum,PageInfo pageInfo) {
        super(data, responseEnum);
        this.total = totalCount;
        this.pageInfo = pageInfo;
        Optional.ofNullable(pageInfo).ifPresent(p -> {
            this.page = p.getPageNum();
            this.size = p.getPageSize();
        });
    }

    public PageResponse<D> pageResponse(IResponseEnum responseEnum) {
        if (Objects.isNull(responseEnum)) {
            return this;
        }
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMsg();
        return this;
    }

    public static <T> PageResponse<T> fail(IResponseEnum responseEnum) {
        return new PageResponse(null, null, responseEnum,null);
    }

    public static <T> PageResponse<T> fail(String resultMessage) {
        return (PageResponse<T>) new PageResponse(null,null, CommonEnum.BIZ_ERROR,null).message(resultMessage);
    }

    public static <T> PageResponse<T> fail(IResponseEnum responseEnum, String resultMessage) {
        return (PageResponse<T>) new PageResponse(null,null, responseEnum,null).message(resultMessage);
    }

    public static <T> PageResponse<T> page(T data) {
        return page(data, null);
    }

    public static <T> PageResponse<T> page(T data, Long totalCount) {
        return new PageResponse(data, totalCount, CommonEnum.SUCCESS,null);
    }

    public static <T> PageResponse<T> page(T data, Long totalCount,PageInfo pageInfo) {
        return new PageResponse(data, totalCount, CommonEnum.SUCCESS,pageInfo);
    }
}
