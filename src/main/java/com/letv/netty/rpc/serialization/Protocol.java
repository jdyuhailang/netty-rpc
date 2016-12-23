package com.letv.netty.rpc.serialization;

import io.netty.buffer.ByteBuf;

/**
 * Description: <br/>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/22 11:15
 */
public interface Protocol {

    Object decode(ByteBuf data, Class clazz);


	Object decode(ByteBuf data, String classTypeName);


	ByteBuf encode(Object obj, ByteBuf buffer);
}
