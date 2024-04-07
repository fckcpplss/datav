package com.longfor.datav.common.vo;

import com.longfor.datav.common.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
 * 统一请求封装
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request<T>{
    @NotNull(message = "参数不能为空")
    private T data;
    private UserDTO userDTO = new UserDTO("system","system");

    public Request(T data){
        this.data = data;
    }
    @Override
    public String toString() {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("Request").append(":");
        sb.append("[data]").append(" = ").append(this.getData()).append(sep);
        return sb.toString();
    }
}
