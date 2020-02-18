package com.el.common.support.bean.core;

/**
 * 2019/10/19
 *
 * @author eddielee
 */
public class ConvertTest {

    private TestBean testBean1(TestBean source) {
        TestBean newTestBean = new TestBean();
        newTestBean.setSett(source.isSett());
        newTestBean.setS(source.getS());
        newTestBean.setName(source.getName());
        newTestBean.setSs(source.getSs());
        newTestBean.setTt(source.getTt());
        return newTestBean;
    }

    private TestBean testBean2(TestBean source) {
        TestBean newTestBean = new TestBean();
        newTestBean.setSett(source.isSett());
        newTestBean.setS(source.getS());
        newTestBean.setName(source.getName());
        newTestBean.setSs(source.getSs());
        newTestBean.setTt(source.getTt());
        return newTestBean;
    }
}
