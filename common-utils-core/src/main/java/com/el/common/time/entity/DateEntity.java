package com.el.common.time.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 开始结束时间
 * 2019/10/5
 *
 * @author eddielee
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateEntity {

    private String startTime;

    private String endTime;
}
