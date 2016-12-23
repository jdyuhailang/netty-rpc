package com.letv.netty.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 15:57
 */
public class LeopardBeanDefinitionParser implements BeanDefinitionParser {
    private static final Logger logger = LoggerFactory.getLogger(LeopardBeanDefinitionParser.class);
    private final Class<?> beanClass;

    private final boolean required;
    public LeopardBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }

    private BeanDefinition parse(Element element, ParserContext parserContext,
                                 Class<?> beanClass, boolean requireId) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        String id = element.getAttribute("id");
        if (StringUtils.isBlank(id) && requireId) {
            throw new IllegalStateException("[Ledo-1032]This bean do not set spring bean id " + id);
        }
        //id 肯定是必须的所以此处去掉对id是否为空的判断
        if (requireId) {
            if (parserContext.getRegistry().containsBeanDefinition(id)) {
                throw new IllegalStateException("[Ledo-1032]Duplicate spring bean id " + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        }
        for (Method setter : beanClass.getMethods()) {
            if (!isProperty(setter, beanClass)) continue;
            String name = setter.getName();
            String property = name.substring(3, 4).toLowerCase() + name.substring(4);
            int proType = getPropertyType(property);
            String value = element.getAttribute(property);
            Object reference = value;
            switch (proType) {
                case 5: // ref
                    if (StringUtils.isNotBlank(value) ) {
                        BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
                        if (!refBean.isSingleton() && beanClass == ProviderBean.class) {
                            throw new IllegalStateException("[Ledo-1033]The exported service ref " + value + " must be singleton! Please set the " + value + " bean scope to singleton, eg: <bean id=\"" + value + "\" scope=\"singleton\" ...>");
                        }
                        reference = new RuntimeBeanReference(value);
                    } else {
                        reference = null;//保持住ref的null值
                    }
                    beanDefinition.getPropertyValues().addPropertyValue(property, reference);
                    break;
                case 14: // server
                    parseMultiRef(property, value, beanDefinition, parserContext);
                    break;
                case 19: // interfaceId --> interface
                    value = element.getAttribute("interface");
                    if (value != null) {
                        beanDefinition.getPropertyValues().addPropertyValue(property, value);
                    }
                    break;
                default:
                    // 默认非空字符串只是绑定值到属性
                    if (StringUtils.isNotBlank(value)) {
                        beanDefinition.getPropertyValues().addPropertyValue(property, reference);
                    }
                    break;
            }
        }
        return beanDefinition;
    }
    /*
     *判断是否有相应get\set方法的property
     */
    private boolean isProperty(Method method, Class beanClass) {
        String methodName = method.getName();
        boolean flag = methodName.length() > 3 && methodName.startsWith("set") && Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 1;
        Method getter = null;
        if (flag) {
            Class<?> type = method.getParameterTypes()[0];
            try {
                getter = beanClass.getMethod("get" + methodName.substring(3));
            } catch (NoSuchMethodException e) {
                try {
                    getter = beanClass.getMethod("is" + methodName.substring(3));
                } catch (NoSuchMethodException e2) {
                }
            }
            flag = getter != null && Modifier.isPublic(getter.getModifiers()) && type.equals(getter.getReturnType());
        }
        return flag;
    }
    /*
     *返回Property Type
     *
     */
    private int getPropertyType(String propertyName) {
        int type = -1;
        if ("registry".equals(propertyName)) {
            type = 1;
        } else if ("protocol".equals(propertyName)) {
            type = 2;
        } else if ("ref".equals(propertyName)) {
            type = 5;
        } else if ("parameters".equals(propertyName)) {
            type = 6;
        } else if ("methods".equals(propertyName)) {
            type = 7;
        } else if ("server".equals(propertyName)) {
            type = 14;
        } else if ("filter".equals(propertyName)) {
            type = 15;
        } else if ("onreturn".equals(propertyName)
                || "onconnect".equals(propertyName)
                || "onavailable".equals(propertyName)
                || "router".equals(propertyName)) {  // 逗号分隔的多个ref
            type = 16;
        } else if ("mockref".equals(propertyName)
                || "cacheref".equals(propertyName)
                || "groupRouter".equals(propertyName)) { // 单个ref
            type = 17;
        } else if ("clazz".equals(propertyName)) {
            type = 18;
        } else if ("interfaceId".equals(propertyName)) {
            type = 19;
        } else if ("providers".equals(propertyName)
                || "consumers".equals(propertyName)) { // 可以将属性置为 空字符串
            type = 20;
        } else if ("consumerConfigs".equals(propertyName)) {
            type = 21;
        }
        return type;
    }
    private void parseMultiRef(String property, String value, RootBeanDefinition beanDefinition,
                               ParserContext parserContext) {
        String[] values = value.split("\\s*[,]+\\s*");
        ManagedList list = null;
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            if (StringUtils.isNotBlank(v)) {
                if (list == null) {
                    list = new ManagedList();
                }
                list.add(new RuntimeBeanReference(v));
            }
        }
        logger.info("bean definition register property {} ,value {}",property,list);
        beanDefinition.getPropertyValues().addPropertyValue(property, list);
    }
}
