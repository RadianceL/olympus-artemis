package com.el.base.utils.support.globalization;

import com.el.base.utils.support.globalization.lang.Local;
import org.jetbrains.annotations.PropertyKey;

/**
 * 多语言文案获取
 *
 * @author eddie.lys
 * @since 3/30/2022
 */
public class GlobalMessage {

    private static final String BUNDLE = "i18n.errors";

    public static String of(@PropertyKey(resourceBundle = BUNDLE) String messageCode, Object... messageFill) {
        return GlobalMessagePool.getMessage(messageCode, Local.CN, messageFill);
    }

}
