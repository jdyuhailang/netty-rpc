package com.letv.netty.rpc.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 14:49
 */
public class ClassTypeUtils {
    /**
     * String-->Class 缓存，指定大小
     */
    private final static Map<String, Class> classMap = new ConcurrentHashMap<String, Class>();

    /**
     * String-->Class 缓存，指定大小
     */
    private final static Map<Class, String> typeStrMap = new ConcurrentHashMap<Class, String>();

    /**
     * Class[]转String[]
     *
     * @param typeStrs
     *         对象描述[]
     * @return Class[]
     */
    public static Class[] getClasses(String[] typeStrs) throws RuntimeException {
        if (CommonUtils.isEmpty(typeStrs)) {
            return new Class[0];
        } else {
            Class[] classes = new Class[typeStrs.length];
            for (int i = 0; i < typeStrs.length; i++) {
                classes[i] = getClass(typeStrs[i]);
            }
            return classes;
        }
    }

    /**
     * String转Class
     *
     * @param typeStr
     *         对象描述
     * @return Class[]
     */
    public static Class getClass(String typeStr) throws RuntimeException {
        try {
            Class clazz = classMap.get(typeStr);
            if (clazz == null) {
                if ("void".equals(typeStr)) clazz = void.class;
                else if ("boolean".equals(typeStr)) clazz = boolean.class;
                else if ("byte".equals(typeStr)) clazz = byte.class;
                else if ("char".equals(typeStr)) clazz = char.class;
                else if ("double".equals(typeStr)) clazz = double.class;
                else if ("float".equals(typeStr)) clazz = float.class;
                else if ("int".equals(typeStr)) clazz = int.class;
                else if ("long".equals(typeStr)) clazz = long.class;
                else if ("short".equals(typeStr)) clazz = short.class;
                else {
                    String jvmName = canonicalNameToJvmName(typeStr);
                    clazz = ClassLoaderUtils.forName(jvmName);
                }
                classMap.put(typeStr, clazz);
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private static String canonicalNameToJvmName(String typeStr) {
        boolean isarray = typeStr.endsWith("[]");
        if (isarray) {
            String t = ""; // 计数，看上几维数组
            while (isarray) {
                typeStr = typeStr.substring(0, typeStr.length() - 2);
                t += "[";
                isarray = typeStr.endsWith("[]");
            }
            if ("boolean".equals(typeStr)) typeStr = t + "Z";
            else if ("byte".equals(typeStr)) typeStr = t + "B";
            else if ("char".equals(typeStr)) typeStr = t + "C";
            else if ("double".equals(typeStr)) typeStr = t + "D";
            else if ("float".equals(typeStr)) typeStr = t + "F";
            else if ("int".equals(typeStr)) typeStr = t + "I";
            else if ("long".equals(typeStr)) typeStr = t + "J";
            else if ("short".equals(typeStr)) typeStr = t + "S";
            else typeStr = t + "L" + typeStr + ";";
        }
        return typeStr;
    }

    /**
     * Class[]转String[] <br>
     * 注意，得到的String可能不能直接用于Class.forName，请使用getClass(String)反向获取
     *
     * @param types
     *         Class[]
     * @return 对象描述
     */
    public static String[] getTypeStrs(Class[] types) {
        if (CommonUtils.isEmpty(types)) {
            return new String[0];
        } else {
            String[] strings = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                strings[i] = getTypeStr(types[i]);
            }
            return strings;
        }
    }

    /**
     * Class转String<br>
     * 注意，得到的String可能不能直接用于Class.forName，请使用getClass(String)反向获取
     *
     * @param clazz
     *         Class
     * @return 对象
     * @see #getClass(String)
     */
    public static String getTypeStr(Class clazz) {
        String typeStr = typeStrMap.get(clazz);
        if (typeStr == null) {
            if (clazz.isArray()) {
                String name = clazz.getName(); // 原始名字：[Ljava.lang.String;
                typeStr = jvmNameToCanonicalName(name); // java.lang.String[]
            } else {
                typeStr = clazz.getName();
            }
            typeStrMap.put(clazz, typeStr);
        }
        return typeStr;
    }

    private static String jvmNameToCanonicalName(String jvmName) {
        boolean isarray = jvmName.charAt(0) == '[';
        if (isarray) {
            String cnName = StringUtils.EMPTY; // 计数，看上几维数组
            int i = 0;
            for (; i < jvmName.length(); i++) {
                if (jvmName.charAt(i) != '[') {
                    break;
                }
                cnName += "[]";
            }
            String componentType = jvmName.substring(i, jvmName.length());
            if ("Z".equals(componentType)) cnName = "boolean" + cnName;
            else if ("B".equals(componentType)) cnName = "byte" + cnName;
            else if ("C".equals(componentType)) cnName = "char" + cnName;
            else if ("D".equals(componentType)) cnName = "double" + cnName;
            else if ("F".equals(componentType)) cnName = "float" + cnName;
            else if ("I".equals(componentType)) cnName = "int" + cnName;
            else if ("J".equals(componentType)) cnName = "long" + cnName;
            else if ("S".equals(componentType)) cnName = "short" + cnName;
            else cnName = componentType.substring(1,componentType.length()-1) + cnName; // 对象的 去掉L
            return cnName;
        }
        return jvmName;
    }
}
