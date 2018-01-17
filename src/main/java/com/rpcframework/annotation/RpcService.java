package com.rpcframework.annotation;

import com.rpcframework.filter.Filter;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标记注解 描述这个类提供rpc服务
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RpcService {

	String value() default "";

	Class filter() default Filter.NONE.class;
}
