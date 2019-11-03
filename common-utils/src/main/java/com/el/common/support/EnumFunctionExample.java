package com.el.common.support;


/**
 * 枚举方法用例
 * 2019/10/7
 *
 * @author eddielee
 */
public enum EnumFunctionExample {
    /**
     * 模版方法用例
     */
    FUNCTION_EXAMPLE {
        @Override
        public void functionExample() {
            // do something
        }
    };

    /**
     * 模版方法用例
     */
    public abstract void functionExample();
}
