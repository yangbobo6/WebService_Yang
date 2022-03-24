package com.yangbo.webserver.core.context.holder;

import com.yangbo.webserver.core.filter.Filter;
import lombok.Data;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-11:21
 * @Description:  拦截器链
 */
@Data
public class FilterHolder {
    private Filter filter;
    private String filterClass;

    public FilterHolder(String filterClass){
        this.filterClass = filterClass;
    }


}
