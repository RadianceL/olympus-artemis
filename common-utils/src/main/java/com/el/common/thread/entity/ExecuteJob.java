package com.el.common.thread.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 执行的任务包装
 * 2019-05-26
 *
 * @author eddielee
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteJob {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务内容
     */
    private Object value;

}
