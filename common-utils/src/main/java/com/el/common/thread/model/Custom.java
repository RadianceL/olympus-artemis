package com.el.common.thread.model;


import com.el.common.thread.entity.ExecuteJob;

/**
 * 消费者
 * 2019-05-27
 *
 * @author eddielee
 */
public interface Custom {

    /**
     * 消费一个产品
     */
    void consumption(ExecuteJob e);

}
