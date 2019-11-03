package com.el.common.support.bean.core;


import org.objectweb.asm.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试
 * 2019/10/19
 *
 * @author eddielee
 */
public class ClassByteConvert implements Opcodes {

    private static final Map<String, String> TYPE_MAP = new ConcurrentHashMap<>(32);

    private static final ClassWriter CLASS_WRITER = new ClassWriter(0);

    static {
        TYPE_MAP.put("java.lang.String", "Ljava/lang/String;");
        TYPE_MAP.put("java.lang.Byte", "Ljava/lang/Byte;");
        TYPE_MAP.put("java.lang.Short", "Ljava/lang/Short;");
        TYPE_MAP.put("java.lang.Integer", "Ljava/lang/Integer;");
        TYPE_MAP.put("java.lang.Long", "Ljava/lang/Long;");
        TYPE_MAP.put("java.lang.Float", "Ljava/lang/Float;");
        TYPE_MAP.put("java.lang.Double", "Ljava/lang/Double;");
        TYPE_MAP.put("java.lang.Character", "Ljava/lang/Character;");
        TYPE_MAP.put("java.lang.Boolean", "Ljava/lang/Boolean;");
        TYPE_MAP.put("byte", "B");
        TYPE_MAP.put("int", "I");
        TYPE_MAP.put("int", "I");
        TYPE_MAP.put("int", "I");
        TYPE_MAP.put("int", "I");
        TYPE_MAP.put("int", "I");
        TYPE_MAP.put("int", "I");
        TYPE_MAP.put("int", "I");
        TYPE_MAP.put("int", "I");

    }

    public static byte[] dump () throws Exception {

        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;
        CLASS_WRITER.visit(52, ACC_PUBLIC + ACC_SUPER, "com/el/util/common/bean/core/Conver", null, "java/lang/Object", null);

        {
            mv = CLASS_WRITER.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = CLASS_WRITER.visitMethod(ACC_PUBLIC, "testBeanConver", "(Lcom/el/util/common/bean/core/TestBean;)Lcom/el/util/common/bean/core/TestBean;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "com/el/util/common/bean/core/TestBean");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "com/el/util/common/bean/core/TestBean", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/el/util/common/bean/core/TestBean", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/el/util/common/bean/core/TestBean", "setName", "(Ljava/lang/String;)V", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/el/util/common/bean/core/TestBean", "getS", "()I", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/el/util/common/bean/core/TestBean", "setS", "(I)V", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/el/util/common/bean/core/TestBean", "isSett", "()Z", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/el/util/common/bean/core/TestBean", "setSett", "(Z)V", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
        CLASS_WRITER.visitEnd();

        return CLASS_WRITER.toByteArray();
    }
}

