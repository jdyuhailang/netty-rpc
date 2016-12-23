package com.letv.netty.rpc.test;

import com.letv.netty.rpc.protocal.RpcSerializeProtocol;
import com.letv.netty.rpc.transport.MessageSendExecutor;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 13:16
 */
public class RpcParallelTest {

    public static void parallelTask(MessageSendExecutor executor, int parallel, String serverAddress, RpcSerializeProtocol protocol) throws InterruptedException {
        //开始计时
        StopWatch sw = new StopWatch();
        sw.start();

        CountDownLatch signal = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(parallel);

        for (int index = 0; index < parallel; index++) {
            CalcParallelRequestThread client = new CalcParallelRequestThread(executor, signal, finish, index);
            new Thread(client).start();
        }

        //10000个并发线程瞬间发起请求操作
        signal.countDown();
        finish.await();
        sw.stop();

        String tip = String.format("[%s] RPC调用总共耗时: [%s] 毫秒", protocol, sw.getTime());
        System.out.println(tip);

    }

    //JDK本地序列化协议
    public static void JdkNativeParallelTask(MessageSendExecutor executor, int parallel) throws InterruptedException {
        String serverAddress = "127.0.0.1:18887";
        RpcSerializeProtocol protocol = RpcSerializeProtocol.JDKSERIALIZE;
        executor.setRpcServerLoader(serverAddress, protocol);
        RpcParallelTest.parallelTask(executor, parallel, serverAddress, protocol);
        TimeUnit.SECONDS.sleep(3);
    }

    //Kryo序列化协议
    public static void KryoParallelTask(MessageSendExecutor executor, int parallel) throws InterruptedException {
        String serverAddress = "127.0.0.1:18888";
        RpcSerializeProtocol protocol = RpcSerializeProtocol.KRYOSERIALIZE;
        executor.setRpcServerLoader(serverAddress, protocol);
        RpcParallelTest.parallelTask(executor, parallel, serverAddress, protocol);
        TimeUnit.SECONDS.sleep(3);
    }

    //Hessian序列化协议
    public static void HessianParallelTask(MessageSendExecutor executor, int parallel) throws InterruptedException {
        String serverAddress = "127.0.0.1:18889";
        RpcSerializeProtocol protocol = RpcSerializeProtocol.HESSIANSERIALIZE;
        executor.setRpcServerLoader(serverAddress, protocol);
        RpcParallelTest.parallelTask(executor, parallel, serverAddress, protocol);
        TimeUnit.SECONDS.sleep(3);
    }

    public static void main(String[] args) throws Exception {

        String serverAddress = "127.0.0.1:18889";

        RpcSerializeProtocol protocol = RpcSerializeProtocol.HESSIANSERIALIZE;
        MessageSendExecutor executor = new MessageSendExecutor(serverAddress,protocol);
        Calculate calc = MessageSendExecutor.execute(Calculate.class);
        int add = calc.add(10, 10);
        System.out.println("calc add result:[" + add + "]");
        //并行度10000
        /*int parallel = 10;
        MessageSendExecutor executor = new MessageSendExecutor();

        for (int i = 0; i < 10; i++) {
            //JdkNativeParallelTask(executor, parallel);
            //KryoParallelTask(executor, parallel);
            HessianParallelTask(executor, parallel);
            System.out.printf("Netty RPC Server 消息协议序列化第[%d]轮并发验证结束!\n\n", i);
        }

        executor.stop();*/
    }
}