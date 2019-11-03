package com.el.common.support.bean.core;

import com.el.common.support.bean.exceptions.BeanFieldDescriptionIsEmptyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * bean语言协议
 * 2019/10/20
 *
 * @author eddielee
 */
public class BeanLanguageProtocol {

    /**
     * 出生标识符
     */
    private static final Character END_FIELD = '#';

    private static final Character FIRST_FIELD = '0';

    /**
     * 子属性标识符
     */
    private static final Character CHILD_FIELD = '.';

    /**
     * 数组 && List标识符
     */
    private static final Character LIST_FIELD_START = '[';

    private static final Character LIST_FIELD_END = ']';

    /**
     * 忽略标识符
     */
    private static final Character IGNORE_FIELD = '?';

    public static List<BeanFieldPositionDefinition> convertBeanLanguageProtocol(String desc){
        if (StringUtils.isBlank(desc)){
            throw new BeanFieldDescriptionIsEmptyException("传入的字段描述为空异常，请检查字段映射注册填写是否完整");
        }
        desc = desc.concat(String.valueOf(END_FIELD));
        boolean runSign = true;
        List<BeanFieldPositionDefinition> beanFieldPositionDefinitions = new ArrayList<>();
        while (runSign){
            BeanFieldDescription fieldDefinition = getFieldDefinition(desc);
            String protocolDescription = String.valueOf(fieldDefinition.protocolDescription);
            String savedDescription = protocolDescription + fieldDefinition.fieldName;
            desc = desc.substring(savedDescription.length());
            beanFieldPositionDefinitions.add(new BeanFieldPositionDefinition(protocolDescription, new FieldDefinition(fieldDefinition.fieldName)));
            if (IGNORE_FIELD.equals(fieldDefinition.protocolDescription)){
                break;
            }
            if (!isContainsProtocolDescription(desc)){
                runSign = false;
            }
        }
        return beanFieldPositionDefinitions;
    }

    private static BeanFieldDescription getFieldDefinition(String desc){
        for (int i = 0; i < desc.length(); i ++){
            if (CHILD_FIELD.equals(desc.charAt(i))){
                return new BeanFieldDescription(desc.substring(0, i), CHILD_FIELD);
            }
            if (LIST_FIELD_START.equals(desc.charAt(i))){
                return new BeanFieldDescription(desc.substring(0, i), LIST_FIELD_START);
            }
            if (LIST_FIELD_END.equals(desc.charAt(i))){
                return new BeanFieldDescription(desc.substring(0, i), LIST_FIELD_END);
            }
            if (IGNORE_FIELD.equals(desc.charAt(i))){
                return new BeanFieldDescription(desc.substring(0, i), IGNORE_FIELD);
            }
            if (END_FIELD.equals(desc.charAt(i))){
                return new BeanFieldDescription(desc.substring(0, i), END_FIELD);
            }
        }
        return new BeanFieldDescription(desc, FIRST_FIELD);
    }


    private static boolean isContainsProtocolDescription(String desc){
        if (StringUtils.isNotBlank(desc)){
            return desc.contains(String.valueOf(CHILD_FIELD))
                    || desc.contains(String.valueOf(LIST_FIELD_START)) || desc.contains(String.valueOf(LIST_FIELD_END))
                    || desc.contains(String.valueOf(IGNORE_FIELD))
                    || desc.contains(String.valueOf(END_FIELD));
        }
        return false;
    }

    /**
     * 字段位置定义
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BeanFieldPositionDefinition{

        /**
         * 协议描述字段
         */
        private String protocolDescription;

        /**
         * 字段定义
         */
        private FieldDefinition fieldDefinition;

    }

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BeanFieldDescription{

        private String fieldName;

        private Character protocolDescription;
    }
}
