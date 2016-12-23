/**
 *  HessianSerializerFactory.java Created on 2014/5/14 10:07
 *
 *  Copyright (c) 2014 by www.le.ledo.com.
 */
package com.letv.netty.rpc.serialization.hessian;


import com.letv.netty.rpc.caucho.hessian.io.SerializerFactory;
import com.letv.netty.rpc.utils.ClassLoaderUtils;

/**
 * Title: <br>
 * <p/>
 * Description: <br>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class HessianSerializerFactory extends SerializerFactory {

    protected static final SerializerFactory SERIALIZER_FACTORY = new HessianSerializerFactory();

    @Override
    public ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(HessianSerializerFactory.class);
    }

}