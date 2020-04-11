package com.el.common.data;

import lombok.Data;

import java.util.Objects;

/**
 * 分页参数
 * since 2020/4/11
 *
 * @author eddie
 */
@Data
public class Pageable {

    /**
     * 分页数据
     */
    private PageInfo pageInfo;

    public void setTotalPage(Integer totalItemSize){
        if (Objects.nonNull(totalItemSize) && this.pageInfo.verify()) {
            pageInfo.setTotalPageSize((int) Math.ceil((double) totalItemSize / this.pageInfo.getCurrentPageSize()));
        }
    }

    public Integer getBeginIndex() {
        if (Objects.nonNull(this.pageInfo) && this.pageInfo.verify()) {
            return (this.pageInfo.getCurrentPage() - 1) * this.pageInfo.getCurrentPageSize();
        }
        return null;
    }

    public Integer getPageSize() {
        if (Objects.nonNull(this.pageInfo) && this.pageInfo.verify()) {
            return this.pageInfo.getCurrentPageSize();
        }
        return null;
    }
}
