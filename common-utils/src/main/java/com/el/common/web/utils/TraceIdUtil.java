package com.el.common.web.utils;


import com.el.base.utils.support.utils.Md5Utils;
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
        return Md5Utils.encode(uuid.concat(time));
    }

}
