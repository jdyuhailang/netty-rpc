package com.letv.netty.rpc.utils;

/**
 * Title: 压缩/解压缩接口<br>
 * <p/>
 * Description: <br>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public interface ICompress {

    /**
     * 压缩
     *
     * @param src
     *         源数据
     * @return 压缩后数据
     */
    public byte[] compress(byte[] src);

    /**
     * @param src
     *         压缩后数据
     * @return 源数据
     */
    public byte[] deCompress(byte[] src);

}
