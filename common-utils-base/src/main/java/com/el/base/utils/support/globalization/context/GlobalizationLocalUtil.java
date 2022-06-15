package com.el.base.utils.support.globalization.context;

import com.el.base.utils.support.globalization.lang.Local;

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

    public static void setLocalLanguage(Local local) {
        LOCAL_LANGUAGE.set(local);
    }

    public static Local getLocalLanguage() {
        if (Objects.isNull(LOCAL_LANGUAGE.get())) {
            return Local.CN;
        }
        return LOCAL_LANGUAGE.get();
    }

    public static Date getCurrentLocalDate() {
        return GlobalizationLocalUtil.getLocalLanguage()
                .getCurrentLocalTime();
    }

    public static void clear() {
        LOCAL_LANGUAGE.remove();
    }
}
