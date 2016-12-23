package com.letv.netty.rpc.utils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 12:09
 */
public class ValueUtil {
    /**
     * short 转 byte数组
     *
     * @param num
     *         short值
     * @return byte[2]
     */
    public static byte[] short2bytes(short num) {
        byte[] result = new byte[2];
        result[0] = (byte) (num >>> 8); //取低8位放到0下标
        result[1] = (byte) (num);      //取高8位放到1下标
        return result;
    }

    /**
     * int 转 byte数组
     *
     * @param num
     *         int值
     * @return byte[4]
     */
    public static byte[] intToBytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) (num >>> 24);//取最高8位放到0下标
        result[1] = (byte) (num >>> 16);//取次高8为放到1下标
        result[2] = (byte) (num >>> 8); //取次低8位放到2下标
        result[3] = (byte) (num);      //取最低8位放到3下标
        return result;
    }

    /**
     * byte数组转int
     *
     * @param ary
     *         byte[4]
     * @return int值
     */
    public static int bytesToInt(byte[] ary) {
        return (ary[3] & 0xFF)
                | ((ary[2] << 8) & 0xFF00)
                | ((ary[1] << 16) & 0xFF0000)
                | ((ary[0] << 24) & 0xFF000000);
    }
}
