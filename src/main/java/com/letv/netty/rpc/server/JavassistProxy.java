package com.letv.netty.rpc.server;

import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;
import com.letv.netty.rpc.utils.ClassLoaderUtils;
import com.letv.netty.rpc.utils.ClassTypeUtils;
import com.letv.netty.rpc.utils.ReflectUtils;
import javassist.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 14:48
 */
public class JavassistProxy {
    private static AtomicInteger counter = new AtomicInteger();

    /**
     * 原始类和代理类的映射
     */
    private static Map<Class, Class> proxyClassMap = new ConcurrentHashMap<Class, Class>();
    /**
     * 取得代理类(javassist方式)
     *
     * @param interfaceClass 接口类
     * @return 接口代理类
     * @throws Exception 生成代理类异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> interfaceClass, Invoker proxyInvoker) throws Exception {
        Class clazz = proxyClassMap.get(interfaceClass);
        if (clazz == null) {
            //生成代理类
            String interfaceName = ClassTypeUtils.getTypeStr(interfaceClass);
            ClassPool mPool = ClassPool.getDefault();
            mPool.appendClassPath(new LoaderClassPath(ClassLoaderUtils.getClassLoader(JavassistProxy.class)));
            CtClass mCtc = mPool.makeClass(interfaceName + "_proxy_" + counter.getAndIncrement());
            mCtc.addInterface(mPool.get(interfaceName));
            mCtc.addField(CtField.make("public " + Invoker.class.getCanonicalName() + " proxyInvoker = null;", mCtc));
            List<String> methodList = createMethod(interfaceClass, mPool);
            for (String methodStr : methodList) {
                mCtc.addMethod(CtMethod.make(methodStr, mCtc));
            }
            clazz = mCtc.toClass();
            //mCtc.writeFile("D://proxy.class//consumer.class");
            proxyClassMap.put(interfaceClass, clazz);
        }
        Object instance = clazz.newInstance();
        clazz.getField("proxyInvoker").set(instance, proxyInvoker);
        return (T) instance;
    }

    private static List<String> createMethod(Class<?> interfaceClass, ClassPool mPool) {
        Method methodAry[] = interfaceClass.getMethods();
        StringBuilder sb = new StringBuilder();
        List<String> resultList = new ArrayList<String>();
        for (Method m : methodAry) {
            Class<?>[] mType = m.getParameterTypes();
            Class<?> returntype = m.getReturnType();

//            String returnString = returntype.equals(void.class) ? "void" : ReflectUtils.getBoxedClass(returntype).getCanonicalName();
            sb.append(Modifier.toString(m.getModifiers()).replace("abstract", "") + " " +
                    ReflectUtils.getName(returntype) + " " + m.getName() + "( ");
            int c = 0;

            for (Class<?> mp : mType) {
                sb.append(" " + mp.getCanonicalName() + " arg" + c + " ,");
//                sb.append(" " + ReflectUtils.getBoxedClass(mp).getCanonicalName() + " arg" + c + " ,");
                c++;
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            Class<?> exceptions[] = m.getExceptionTypes();
            if (exceptions.length > 0) {
                sb.append(" throws ");
                for (Class<?> exception : exceptions) {
                    sb.append(exception.getCanonicalName() + " ,");
                }
                sb = sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("{");

            sb.append(" Class clazz = " + interfaceClass.getCanonicalName() + ".class;");
            sb.append(" String methodName = \"" + m.getName() + "\";");
            sb.append(" Class[] paramTypes = new Class[" + c + "];");
            sb.append(" Object[] paramValues = new Object[" + c + "];");
            for (int i = 0; i < c; i++) {
                sb.append("paramValues[" + i + "] = ($w)$" + (i + 1) + ";");
//                sb.append("paramTypes[" + i + "] = arg" + i + ".getClass();");
//                sb.append("paramTypes[" + i + "] = " + ClassTypeUtils.class.getCanonicalName() + ".getClass(\""
//                                + mType[i].getCanonicalName() + "\");");
                sb.append("paramTypes[" + i + "] = " + mType[i].getCanonicalName() + ".class;");
//				sb.append("paramTypes[" + i + "] = " + (mType[i].isPrimitive() ? mType[i].getCanonicalName() + ".class;"
//                                                : "arg" + i + ".getClass();"));
            }

//            RequestMessage requestMessage = MessageBuilder.buildRequest(clazz, methodName, paramTypes, paramValues);
//            ResponseMessage responseMessage = proxyInvoker.invoke(requestMessage);
//            if(responseMessage.isError()){
//                throw responseMessage.getException();
//            }
//            return responseMessage.getResponse();

            sb.append(RequestMessage.class.getCanonicalName() + " requestMessage = " +
                    BaseMessage.class.getCanonicalName() +
                    ".buildRequest(clazz, methodName, paramTypes, paramValues);");
            sb.append(ResponseMessage.class.getCanonicalName() + " responseMessage = " +
                    "proxyInvoker.invoke(requestMessage);");
            sb.append("if(responseMessage.isError()){ throw responseMessage.getException(); }");

            if (returntype.equals(void.class)) {
                sb.append(" return;");
            } else {
                sb.append(" return ").append(asArgument( returntype, "responseMessage.getResponse()")).append(";");
            }

            sb.append("}");
            resultList.add(sb.toString());
            sb.delete(0, sb.length());
        }
        return resultList;
    }

    private static String asArgument(Class<?> cl, String name)	{
        if( cl.isPrimitive() )	{
            if( Boolean.TYPE == cl )
                return name + "==null?false:((Boolean)" + name + ").booleanValue()";
            if( Byte.TYPE == cl )
                return name + "==null?(byte)0:((Byte)" + name + ").byteValue()";
            if( Character.TYPE == cl )
                return name + "==null?(char)0:((Character)" + name + ").charValue()";
            if( Double.TYPE == cl )
                return name + "==null?(double)0:((Double)" + name + ").doubleValue()";
            if( Float.TYPE == cl )
                return name + "==null?(float)0:((Float)" + name + ").floatValue()";
            if( Integer.TYPE == cl )
                return name + "==null?(int)0:((Integer)" + name + ").intValue()";
            if( Long.TYPE == cl )
                return name + "==null?(long)0:((Long)" + name + ").longValue()";
            if( Short.TYPE == cl )
                return name + "==null?(short)0:((Short)" + name + ").shortValue()";
            throw new RuntimeException(name+" is unknown primitive type.");
        }
        return "(" + ReflectUtils.getName(cl) + ")"+name;
    }
}
