package com.longfor.datav.common.vo.req;

import com.longfor.datav.common.vo.PageInfo;
import com.longfor.datav.common.vo.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求统一分装
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest<T> extends Request<T> {
    private PageInfo pageInfo = new PageInfo();

    @Override
    public String toString() {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("PageRequest").append(":");
        sb.append("[data]").append(" = ").append(this.getData()).append(sep);
        sb.append("[pageInfo]").append(" = ").append(this.getPageInfo()).append(sep);
        return sb.toString();
    }


    public PageRequest(T data){
        this.setData(data);
    }
    public PageRequest(PageInfo pageInfo,T data){
        this.pageInfo = pageInfo;
        this.setData(data);
    }

    public PageRequest(Integer pageNum,Integer pageSize,T data){
        this.pageInfo = new PageInfo(pageNum,pageSize);
        this.setData(data);
    }
}
