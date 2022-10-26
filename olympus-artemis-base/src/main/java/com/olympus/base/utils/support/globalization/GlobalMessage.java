package com.olympus.base.utils.support.globalization;

import com.olympus.base.utils.support.globalization.context.GlobalizationLocalUtil;
import org.jetbrains.annotations.PropertyKey;

/**
 * 多语言文案获取
 *
 * @author eddie.lys
 * @since 3/30/2022
 */
public class GlobalMessage {

    private static final String BUNDLE = "i18n.i18n_default";

    public static String of(@PropertyKey(resourceBundle = BUNDLE) String messageCode, Object... messageFill) {
        return GlobalMessagePool.getMessage(messageCode, GlobalizationLocalUtil.getLocalLanguage(), messageFill);
    }
}
