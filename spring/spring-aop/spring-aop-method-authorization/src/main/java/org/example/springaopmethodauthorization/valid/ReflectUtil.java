package org.example.springaopmethodauthorization.valid;

import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author clearmind
 */
public class ReflectUtil {


    /**
     * 根据方法参数和字段路径查找对象中的属性值。
     *
     * @param method    方法对象
     * @param args      方法参数数组
     * @param fieldName 字段路径，例如 "nestedObject.field"
     * @return 找到的属性值，如果找不到则返回 null
     */
    public static Object findMethodField(Method method, Object[] args, String fieldName) {
        // 获取参数名称
        StandardReflectionParameterNameDiscoverer parameterNameDiscoverer = new StandardReflectionParameterNameDiscoverer();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        if (paramNames == null || paramNames.length == 0) {
            return null;
        }
        // 将字段路径拆分
        String[] fieldNames = fieldName.split("\\.");
        if (fieldNames.length == 0) {
            return null;
        }
        int entryIndex = -1;
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equalsIgnoreCase(fieldNames[0])) {
                entryIndex = i;
                break;
            }
        }
        if (entryIndex < 0 || entryIndex >= args.length) {
            return null;
        }
        if (fieldNames.length == 1) {
            return args[entryIndex];
        }
        Object currentObject = args[entryIndex];
        for (int i = 1; i < fieldNames.length; i++) {
            try {
                Field field = currentObject.getClass().getDeclaredField(fieldNames[i]);
                ReflectionUtils.makeAccessible(field);
                currentObject = field.get(currentObject);
            } catch (Exception ignored) {
                currentObject = null;
            }
            if (currentObject == null) {
                break;
            }
        }
        return currentObject;
    }
}
