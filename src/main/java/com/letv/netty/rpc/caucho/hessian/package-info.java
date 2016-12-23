/**
 * 为防止包冲突，将hessian打包进来，基于hessian-3.2.1
 */
package com.letv.netty.rpc.caucho.hessian;

/**
修改记录：
1. 增加com.le.ledo.com.caucho.hessian.HessianObjectMapping 类， 保存映射关系
2. 修改com.le.ledo.com.caucho.hessian.io.Hessian2Input 的 readObjectDefinition 方法读取映射关系
3. 优化com.le.ledo.com.caucho.hessian.io.SerializerFactory里的HashMap 变成 ConcurrentHashMap
4. 修改com.le.ledo.com.caucho.hessian.io.Hessian2Input 的 parseChar方法有bug  修复
*/