package com.letv.netty.rpc.serialization.hessian;

import com.letv.netty.rpc.caucho.hessian.io.AbstractHessianInput;
import com.letv.netty.rpc.caucho.hessian.io.AbstractHessianOutput;
import com.letv.netty.rpc.caucho.hessian.io.Hessian2Input;
import com.letv.netty.rpc.caucho.hessian.io.Hessian2Output;
import com.letv.netty.rpc.message.Invocation;
import com.letv.netty.rpc.message.MessageHeader;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;
import com.letv.netty.rpc.serialization.Codec;
import com.letv.netty.rpc.utils.CommonUtils;
import com.letv.netty.rpc.utils.Constants;
import com.letv.netty.rpc.utils.ReflectUtils;
import com.letv.netty.rpc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Title: <br>
 * <p/>
 * Description: <br>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class HessianCodec implements Codec {

    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(HessianCodec.class);



    /**
     * Instantiates a new Hessian codec.
     */
    public HessianCodec() {

    }

    /**
     * Encode byte [ ].
     *
     * @param obj the obj
     * @return the byte [ ]
     */
    public byte[] encode(Object obj) {
        UnsafeByteArrayOutputStream bos = new UnsafeByteArrayOutputStream(1024);
        Hessian2Output mH2o = new Hessian2Output(bos);
        mH2o.setSerializerFactory(HessianSerializerFactory.SERIALIZER_FACTORY);
        if (obj instanceof RequestMessage) {
            try {
                encodeRequest((RequestMessage) obj, mH2o);
                mH2o.flushBuffer();

                byte[] data = bos.toByteArray();
                return data;
            } catch (Exception e) {
            }
        } else if (obj instanceof ResponseMessage) {
            try {
                encodeResponse((ResponseMessage) obj, mH2o);
                mH2o.flushBuffer();

                byte[] data = bos.toByteArray();
                return data;
            } catch (Exception e) {
            }
        } else {
            try {
                mH2o.writeObject(obj);
                mH2o.flushBuffer();
            } catch (Exception e) {
            }
        }
        byte[] data = bos.toByteArray();
        return data;
    }

    public byte[] encode(Object obj, String classTypeName) {
        return encode(obj);
    }

    /**
     * 编码发送给服务端的Request
     *
     * @param req the RequestMessage
     * @param out the Hessian2Output
     */
    private void encodeRequest(RequestMessage req, Hessian2Output out) throws IOException {
        if(req.isHeartBeat()){
            out.writeObject(null);
        } else {
            Invocation inv = req.getInvocationBody();
            MessageHeader msgHeader = req.getMsgHeader();
            String alias = req.getAlias();
            String[] groupversion = alias.split(":", -1);
            if (groupversion.length != 2) {
                /*throw new LedoCodecException("dubbo protocol need group and version, " +
                        "need config it at <ledo:consumer alias=\"group:version\" />");*/
            }
            String group = groupversion[0];
            String version = groupversion[1];

            out.writeString("2.4.10");
            out.writeString(req.getClassName());
            out.writeString(version);
            out.writeString(inv.getMethodName());

            Object[] args = inv.getArgs();
            Class<?>[] pts = inv.getArgClasses();
            out.writeString(ReflectUtils.getDesc(pts));
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    out.writeObject(args[i]);
                }
            }
            inv.addAttachment("group", group);
            inv.addAttachment("path", inv.getClazzName());
            inv.addAttachment("interface", inv.getClazzName());
            inv.addAttachment("version", version);
            inv.addAttachment("timeout", msgHeader.getAttrByKey(Constants.HeadKey.timeout));
            out.writeObject(inv.getAttachments()); // dubbo只支持 Map<String,String>
        }
    }

    /**
     * 编码返回给客户端的response
     *
     * @param res the res
     * @param out the out
     * @throws IOException the iO exception
     */
    private void encodeResponse(ResponseMessage res, AbstractHessianOutput out) throws IOException {
        if(res.isHeartBeat()){
            out.writeObject(null);
        } else {
            // encode response data or error message.
            Throwable th = res.getException();
            if (th == null) {
                Object ret = res.getResponse();
                if (ret == null) {
                    out.writeInt(RESPONSE_NULL_VALUE);
                } else {
                    out.writeInt(RESPONSE_VALUE);
                    out.writeObject(ret);
                }
            } else {
                out.writeInt(RESPONSE_WITH_EXCEPTION);
                out.writeObject(th);
            }
        }
    }

    /**
     * Decode object.
     *
     * @param buf the buf
     * @param clazz the clazz
     * @return the object
     */
    public Object decode(byte[] buf, Class clazz) {
        InputStream is = new UnsafeByteArrayInputStream(buf);
        AbstractHessianInput mH2i = new Hessian2Input(is);
        mH2i.setSerializerFactory(HessianSerializerFactory.SERIALIZER_FACTORY);

        Object obj = null;
        if (clazz == null) {
            try {
                obj = mH2i.readObject(); // 无需依赖class
            } catch (Exception e) {
            }
        } else if (clazz == RequestMessage.class) {
            try {
                RequestMessage req = decodeRequest(buf, mH2i);
                obj = req;
            } catch (Exception e) {
            }
        } else if (clazz == ResponseMessage.class) {
            try {
                ResponseMessage res = decodeResponse(buf, mH2i);
                obj = res;
            } catch (Exception e) {
            }
        } else {
            try {
                obj = mH2i.readObject(); // 无需依赖class
            } catch (Exception e) {
            }
        }

        return obj;
    }

    /**
     * 解码客户端发来的RequestMessage.
     *
     * @param databs the databs
     * @param input the input
     * @return the request message
     * @throws IOException the iO exception
     */
    private RequestMessage decodeRequest(byte[] databs, AbstractHessianInput input) throws IOException {

        RequestMessage req = new RequestMessage();
        Invocation invocation = new Invocation();

        String dubboVersion = input.readString();
        String path = input.readString();
        String version = input.readString();
        String methodName = input.readString();
        try {
            Object[] args;
            Class<?>[] pts;
            String desc = input.readString();
            if (desc.length() == 0) {
                pts = Codec.EMPTY_CLASS_ARRAY;
                args = Codec.EMPTY_OBJECT_ARRAY;
            } else {
                pts = ReflectUtils.desc2classArray(desc);
                args = new Object[pts.length];
                for (int i = 0; i < args.length; i++) {
                    try {
                        args[i] = input.readObject(pts[i]);
                    } catch (Exception e) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Decode argument failed: " + e.getMessage(), e);
                        }
                    }
                }
            }
            // dubbo只支持 Map<String,String>
            Map<String, Object> map = (Map<String, Object>) input.readObject(Map.class);
            if (CommonUtils.isNotEmpty(map)) {
                Object generic = map.get(Constants.CONFIG_KEY_GENERIC);
                if (generic != null && generic instanceof String) {
                    map.put(Constants.CONFIG_KEY_GENERIC, Boolean.valueOf((String) generic));
                }
                String token = (String) map.get("jdtoken");
                if (StringUtils.isNotEmpty(token)) {
                    map.put(".token", token);
                    map.remove("jdtoken");
                }
                invocation.addAttachments(map);
            }
            invocation.addAttachment("dubboVersion", dubboVersion);
            //decode argument ,may be callback
//            for (int i = 0; i < args.length; i++) {
//                args[i] = decodeInvocationArgument(channel, this, pts, i, args[i]);
//            }

            invocation.setArgsType(pts);
            invocation.setMethodName(methodName);
            invocation.setArgs(args);

            req.setInvocationBody(invocation);

            String interfaceId = null;
            String group = null;
            if (map != null) {
                interfaceId = (String) map.get("interface");
                group = (String) map.get("group");
                // version = (String) map.get("version");
            }
            req.setClassName(interfaceId == null ? path : interfaceId);
            req.setAlias(group == null ? version : group + ":" + version);

        } catch (ClassNotFoundException e) {
        }

        return req;
    }


    /**
     * 解码服务端返回的Response
     *
     * @param buf the buf
     * @param in the in
     * @return the response message
     * @throws IOException the iO exception
     */
    private ResponseMessage decodeResponse(byte[] buf, AbstractHessianInput in) throws IOException {
        ResponseMessage res = new ResponseMessage();
        byte staus = (byte) in.readInt();
        switch (staus) {
            case RESPONSE_NULL_VALUE:
                break;
            case RESPONSE_VALUE:
                res.setResponse(in.readObject());
                break;
            case RESPONSE_WITH_EXCEPTION:
                res.setException((Throwable) in.readObject());
                break;
            default:
                break;
        }
        return res;
    }

    /**
     * Decode object.
     *
     * @param buf the buf
     * @param classTypeName the class type name
     * @return the object
     */
    public Object decode(byte[] buf, String classTypeName) {
        InputStream is = new UnsafeByteArrayInputStream(buf);
        AbstractHessianInput mH2i = new Hessian2Input(is);
        mH2i.setSerializerFactory(HessianSerializerFactory.SERIALIZER_FACTORY);

        Object obj = null;
        if (classTypeName == null) {
            try {
                obj = mH2i.readObject(); // 无需依赖class
            } catch (Exception e) {
            }
        } else if (RequestMessage.class.getCanonicalName().equals(classTypeName)) {
            try {
                RequestMessage req = decodeRequest(buf, mH2i);
                obj = req;
            } catch (Exception e) {
            }
        } else if (ResponseMessage.class.getCanonicalName().equals(classTypeName)) {
            try {
                ResponseMessage res = decodeResponse(buf, mH2i);
                obj = res;
            } catch (Exception e) {
            }
        } else {
            try {
                obj = mH2i.readObject(); // 无需依赖class
            } catch (Exception e) {
            }
        }

        return obj;
    }
}