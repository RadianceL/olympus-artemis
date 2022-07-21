package com.olympus.base.utils.validate;

import com.olympus.base.utils.collection.CollectionUtils;
import com.google.common.annotations.Beta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 成员校验工具 <br/>
 * 未测试 - 不建议使用
 * since 2020/8/10
 *
 * @author eddie.lys
 */
@Beta
@Slf4j
public class FieldValidateUtils {

    public static void validate(Object data) throws ValidateMismatchException {
        if (Objects.isNull(data)) {
            return;
        }

        Class<?> dataClass = data.getClass();
        Field[] allFields = FieldUtils.getAllFields(dataClass);
        if (CollectionUtils.isEmpty(allFields)) {
            return;
        }

        for (Field field : allFields) {
            FieldValidate annotation = field.getAnnotation(FieldValidate.class);
            if (Objects.isNull(annotation)){
                continue;
            }

            FieldValidateMethod fieldValidateMethod = annotation.value();
            try {
                Object o = FieldUtils.readField(field, data, true);
                if (Objects.isNull(o)) {
                    if (FieldValidateMethod.UNCHECK.equals(annotation.value())) {
                        continue;
                    }else {
                        throw new ValidateMismatchException(field.getClass(), field.getName(), "FieldValidateMethod is not uncheck and target is null");
                    }
                }
                if (!field.getType().isPrimitive() && isNotPrimitivePackageType(o)) {
                    if (!fieldValidateMethod.validate(o)) {
                        throw new ValidateMismatchException(field.getClass(), field.getName(), "target field mismatch");
                    }
                }else {
                    validate(o);
                }
            } catch (IllegalAccessException e) {
                log.error("can not read target field: has not access permissions", e);
            }
        }
    }

    private static boolean isNotPrimitivePackageType(Object o) {
        if (Objects.isNull(o)) {
            return false;
        }
        return !(o instanceof String)
               && !(o instanceof Double)
               && !(o instanceof Integer)
               && !(o instanceof Float)
               && !(o instanceof Character)
               && !(o instanceof Boolean)
               && !(o instanceof Long)
               && !(o instanceof Byte)
               && !(o instanceof Short);
    }
}
