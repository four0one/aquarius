package com.rpcframework.annotation;

import java.lang.annotation.*;

/**
 * 客户端注入rpc服务属性注解
 * @author wei.chen1
 * @since 2018/1/16
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcResource {
	String value() default "";
}
