package com.letv.netty.rpc.caucho.hessian;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: 保存一些新旧类的映射关系<br>
 * <p/>
 * Description: 例如旧的发来com.xxx.Obj，需要拿com.yyy.Obj去解析，则可以使用此类<br>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class HessianObjectMapping {

    /**
     * 保留映射关系 旧类-->新类
     */
    private final static ConcurrentHashMap<String, String> objectMap = new ConcurrentHashMap<String, String>();

    /**
     * Registry mapping.
     *
     * @param oldclass
     *         the oldclass
     * @param newclass
     *         the newclass
     */
    public static void registryMapping(String oldclass, String newclass) {
        objectMap.put(oldclass, newclass);
    }

    /**
     * Unregistry mapping.
     *
     * @param oldclass
     *         the oldclass
     */
    public static void unregistryMapping(String oldclass) {
        objectMap.remove(oldclass);
    }

    /**
     * Check mapping.
     *
     * @param clazz
     *         the clazz
     * @return the string
     */
    public static String checkMapping(String clazz) {
        if (objectMap.isEmpty()) {
            return clazz;
        }
        String mapclazz = objectMap.get(clazz);
        return mapclazz != null ? mapclazz : clazz;
    }
}