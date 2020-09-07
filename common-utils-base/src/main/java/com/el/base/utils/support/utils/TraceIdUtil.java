package com.el.base.utils.support.utils;

import com.el.common.time.LocalTimeUtils;

import java.util.UUID;

/**
 * since 2020/3/8
 *
 * @author eddie
 */
public class TraceIdUtil {

    public static String getTraceId(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String time = LocalTimeUtils.nowTime();
        return Md5.encode(uuid.concat(time));
    }

}
