package com.letv.netty.rpc.utils;

import com.letv.netty.rpc.core.error.InitErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 12:02
 */
public class CompressUtil {
    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(CompressUtil.class);

    /**
     * 全局参数，是否启动压缩
     * @see Constants#SETTING_INVOKE_CP_OPEN
     */
    public static boolean compressOpen = true;

    /**
     * 全局参数，启动压缩的起点大小（大于这个值才进行压缩）
     * @see Constants#SETTING_INVOKE_CP_SIZE
     */
    public static int compressThresholdSize = 2048;

    public static byte[] compress(byte[] src, byte compressType) {
        ICompress compress = getCompressor(compressType);
        if (compress == null) {
            return src;
        } else {
            return compress.compress(src);
        }
    }

    public static byte[] deCompress(byte[] src, byte compressType) {
        ICompress compress = getCompressor(compressType);
        if (compress == null) {
            return src;
        } else {
            return compress.deCompress(src);
        }
    }

    private static ICompress getCompressor(byte compressType) {
        ICompress compress = null;
        switch (Constants.CompressType.valueOf(compressType)) {
            case lzma:
                compress = LedoQuickLZUtil.getInstance();
                break;
            case snappy:
                compress = Snappy.getInstance();
                break;
//            case lzo:
//                LOGGER.info("compress type  lzo is not yet implemented");
//                break;
//            case gzip:
//                LOGGER.info("compress type  gzip is not yet implemented");
//                break;
//            case zlib:
//                LOGGER.info("compress type  zlib is not yet implemented");
//                break;
            case NONE:
                break;
            default:
                throw new InitErrorException("Unkown compress algorithm :" + compressType);
        }
        return compress;
    }
}
