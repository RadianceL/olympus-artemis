package com.el.common.thread.model;


import com.el.common.thread.entity.ExecuteJob;

/**
 * 生产者
 * 2019-05-27
 *
 * @author eddielee
 */
public interface Producer {

    /**
     * 生产一个产品
     * @return 产品
     */
    ExecuteJob manufacture();

}
