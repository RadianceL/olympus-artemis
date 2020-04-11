package com.el.common.data;

import lombok.Data;

import java.util.Objects;

/**
 * 分页信息
 * since 2020/4/11
 *
 * @author eddie
 */
@Data
public class PageInfo {

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 当前页大小
     */
    private Integer currentPageSize;

    /**
     * 总页码数
     */
    private Integer totalPageSize;

    public boolean verify(){
        return Objects.nonNull(this.currentPage)
                && Objects.nonNull(this.currentPageSize);
    }

}
