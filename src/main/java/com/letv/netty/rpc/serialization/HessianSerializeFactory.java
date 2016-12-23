package com.letv.netty.rpc.serialization;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/22
 * Time: 12:59
 */
public class HessianSerializeFactory extends BasePooledObjectFactory<HessianSerialize> {

    public HessianSerialize create() throws Exception {
        return createHessian();
    }

    public PooledObject<HessianSerialize> wrap(HessianSerialize hessian) {
        return new DefaultPooledObject<HessianSerialize>(hessian);
    }

    private HessianSerialize createHessian() {
        return new HessianSerialize();
    }
}