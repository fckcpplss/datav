package com.longfor.datav.common.vo;

import com.longfor.datav.common.enums.CommonEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 统一返回封装
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<D> implements Serializable {
    private static final int SUCCESS_CODE = 200;
    private static final long serialVersionUID = 8666230768349753573L;
    protected D data;
    protected int code;
    protected String message;
    protected String curTime;
    protected transient boolean showLog = true;

    public Response(D data) {
        this.data = data;
    }

    public Response<D> code(int code) {
        this.code = code;
        return this;
    }

    public Response<D> message(String message) {
        this.message = message;
        return this;
    }

    public Response<D> curTime(String curTime) {
        this.curTime = curTime;
        return this;
    }

    public Response<D> data(D data) {
        this.data = data;
        return this;
    }

    public Response(IResponseEnum responseEnum) {
        this.setResponse(responseEnum);
    }

    public Response(D data, IResponseEnum responseEnum) {
        this.setResponse(responseEnum);
        this.data = data;
    }

    public Response<D> response(IResponseEnum responseEnum) {
        if (Objects.isNull(responseEnum)) {
            return this;
        }
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMsg();
        return this;
    }

    public void setResponse(IResponseEnum responseEnum) {
        if (Objects.nonNull(responseEnum)) {
            this.code = responseEnum.getCode();
            this.message = responseEnum.getMsg();
        }
    }

    public boolean valueOfShowLog() {
        return this.showLog;
    }

    public static <T> Response<T> ok() {
        return new Response(null, CommonEnum.SUCCESS);
    }

    public static <T> Response<T> ok(T data) {
        return new Response(data, CommonEnum.SUCCESS);
    }

    public static <T> Response<T> fail(T data,IResponseEnum responseEnum) {
        return new Response(data,responseEnum);
    }

    public static <T> Response<T> fail(IResponseEnum responseEnum) {
        return new Response(responseEnum);
    }

    public static <T> Response<T> fail(String resultMessage) {
        return new Response(CommonEnum.BIZ_ERROR).message(resultMessage);
    }

    public static <T> Response<T> fail(IResponseEnum responseEnum, String resultMessage) {
        return new Response(responseEnum).message(resultMessage);
    }
    public static <T> Response<T> fail(int code, String message) {
        return fail(new IResponseEnum() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMsg() {
                return message;
            }
        });
    }

    public static <T> PageResponse<T> page(T data) {
        return page(data, (Long) null);
    }

    public static <T> PageResponse<T> page(T data, Long totalCount) {
        return new PageResponse(data, totalCount, CommonEnum.SUCCESS,null);
    }

    public static <T> PageResponse<T> page(T data, Long totalCount,PageInfo pageInfo) {
        return new PageResponse(data, totalCount, CommonEnum.SUCCESS,pageInfo);
    }
}