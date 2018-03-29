package com.rpcframework.core;

import com.rpcframework.annotation.RpcResource;
import com.rpcframework.client.ClientController;
import com.rpcframework.service.DemoService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */

public class RpcClientBootstrapTest {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-config-client.xml");
        ClientController bean = context.getBean(ClientController.class);
        ClientRunnable clientRunnable = new ClientRunnable(bean);
        long startTime = System.currentTimeMillis();
        System.out.println("开始时间" + startTime);
        Thread t;
        for (int i = 0; i < 20000; i++) {
            t = new Thread(clientRunnable);
            t.start();
        }
        try {
            bean.getRunThreads().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();

        System.out.println("共用时间：" + (endTime - startTime));


		/*ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-config-client.xml");
		ClientController bean = context.getBean(ClientController.class);
		bean.say();*/
    }


}