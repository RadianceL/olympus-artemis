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

    private String name;

    private Object value;

}
