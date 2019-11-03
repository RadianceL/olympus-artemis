package com.el.common.support.bean;

import com.el.common.support.bean.core.BeanLanguageProtocol;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象差异定义结构
 * 2019/10/20
 *
 * @author eddielee
 */
public class DiffDefinition {


    public DiffDefinition(Builder builder){

    }

    @Data
    private static class Builder{

        private Map<String, Node<Class<?>>> definitionMap = new HashMap<>();

        private Class<?> sourceClass;

        private Class<?> targetClass;

        public Builder registerClass(Class<?> sourceClass, Class<?> targetClass){
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
            return this;
        }

        public Builder registerField(String sourceField, String targetField){
            List<BeanLanguageProtocol.BeanFieldPositionDefinition> sourceFieldDesc = BeanLanguageProtocol.convertBeanLanguageProtocol(sourceField);
            List<BeanLanguageProtocol.BeanFieldPositionDefinition> targetFieldDesc = BeanLanguageProtocol.convertBeanLanguageProtocol(targetField);

            return this;
        }

        public DiffDefinition registerFieldFinish(){
            System.out.println(definitionMap.toString());
            return new DiffDefinition(this);
        }

        private static boolean isBaseType(Object object) {
            Class<?> className = object.getClass();
            return className.equals(Integer.class) ||
                    className.equals(Byte.class) ||
                    className.equals(Long.class) ||
                    className.equals(Double.class) ||
                    className.equals(Float.class) ||
                    className.equals(Character.class) ||
                    className.equals(Short.class) ||
                    className.equals(Boolean.class) ||
                    className.equals(String.class);
        }

    }

    /**
     * 内部节点
     * @param <T>
     */
    @Data
    private static class Node<T>{

        /**
         * 索引
         */
        private T key;

        /**
         * 被拷贝对象的字段名称
         */
        private String sourceField;

        /**
         * 目标对象的字段名称
         */
        private String targetField;

    }

}
