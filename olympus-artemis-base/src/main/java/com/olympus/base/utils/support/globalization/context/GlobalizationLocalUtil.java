package com.olympus.base.utils.support.globalization.context;

import com.olympus.base.utils.support.globalization.lang.Local;

import java.util.Date;
import java.util.Objects;

/**
 * 多语言本地上下文
 * since 4/13/22
 *
 * @author eddie
 */
public class GlobalizationLocalUtil {

    /**
     * 本地语言
     */
    private static final ThreadLocal<Local> LOCAL_LANGUAGE = new ThreadLocal<>();
    /**
     * 本地时间
     */
    private static final ThreadLocal<Local> TIME_LOCAL = new ThreadLocal<>();

    public static void setLocalLanguage(Local local) {
        LOCAL_LANGUAGE.set(local);
    }

    public static void setLocalTime(Local local) {
        TIME_LOCAL.set(local);
    }

    public static Local getLocalLanguage() {
        if (Objects.isNull(LOCAL_LANGUAGE.get())) {
            return Local.CN;
        }
        return LOCAL_LANGUAGE.get();
    }


    public static Local getLocalTime() {
        if (Objects.isNull(TIME_LOCAL.get())) {
            return Local.CN;
        }
        return TIME_LOCAL.get();
    }

    public static Date getCurrentLocalDate() {
        return GlobalizationLocalUtil.getLocalTime()
                .getCurrentLocalTime();
    }

    public static Long getCurrentLocalTimeMillis() {
        return GlobalizationLocalUtil.getLocalTime()
                .getCurrentLocalTime().getTime();
    }

    public static void clear() {
        LOCAL_LANGUAGE.remove();
        TIME_LOCAL.remove();
    }
}
