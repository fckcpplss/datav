package com.longfor.datav.admin.exception;

import com.longfor.datav.common.vo.IResponseEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 描述:自定义统一的异常
 *
 * @author zhaoyalong
 * @date 2022-06-20 下午13:35
 */
@Getter
@Setter
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -2054023852066901556L;
    protected IResponseEnum responseEnum;
    protected Object[] args;
    protected Object data;

    public BusinessException(String msg) {
        super(msg);
        this.responseEnum = new IResponseEnum() {
            @Override
            public int getCode() {
                return -1;
            }
            @Override
            public String getMsg() {
                return msg;
            }
        };
    }

    public BusinessException(int code, String msg){
        super(msg);
        this.responseEnum = new IResponseEnum() {
            @Override
            public int getCode() {
                return code;
            }
            @Override
            public String getMsg() {
                return msg;
            }
        };
    }



    public BusinessException(int code, String msg,Object data){
        super(msg);
        this.responseEnum = new IResponseEnum() {
            @Override
            public int getCode() {
                return code;
            }
            @Override
            public String getMsg() {
                return msg;
            }
        };
        this.data = data;
    }

    public BusinessException(IResponseEnum responseEnum){
        super(responseEnum.getMsg());
        this.responseEnum = responseEnum;
    }

    public BusinessException(IResponseEnum responseEnum,Object data){
        super(responseEnum.getMsg());
        this.responseEnum = responseEnum;
        this.data = data;
    }

    public BusinessException(IResponseEnum responseEnum, String msg){
        super(msg);
        this.responseEnum = responseEnum;
    }

    public BusinessException(IResponseEnum responseEnum, Object[] args, String msg){
        super(msg);
        this.responseEnum = responseEnum;
        this.args=args;
    }

    public BusinessException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause){
        super(message,cause);
        this.responseEnum = responseEnum;
        this.args=args;
    }

}
