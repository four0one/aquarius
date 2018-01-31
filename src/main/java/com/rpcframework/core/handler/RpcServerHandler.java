package com.rpcframework.core.handler;

import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.RpcResponse;
import com.rpcframework.core.heartbeat.Ping;
import com.rpcframework.utils.ServiceSignUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 服务端接受请求处理器
 *
 * @author wei.chen1
 * @since 2018/1/15
 */
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ExecutorService serviceCallThreadPool = Executors.newFixedThreadPool(8);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof Ping) {
			ctx.fireChannelRead(msg);
			return;
		}
		if (msg instanceof RpcRequest) {
			logger.debug("request:{}", msg);
			RpcRequest request = (RpcRequest) msg;
			//开启线程调用服务
//			Future<RpcResponse> responseFuture = serviceCallThreadPool.submit(new ServiceInvokeCallable(request));
//			RpcResponse response = responseFuture.get();
			RpcResponse response = new RpcResponse();
			response.setRequestId(request.getRequestId());
			String serviceName = ServiceSignUtils.sign(request.getService(), request.getMethodName());
			Object target = RpcServiceContext.getRpcTarget(serviceName);
			Class<?> targetClass = Class.forName(request.getService());
			Method method = targetClass.getMethod(request.getMethodName(), request.getParameterTypes());
			Object result = ReflectionUtils.invokeMethod(method, target, request.getParameters());
			response.setResult(result);
			ctx.writeAndFlush(response);
			return;
		}
	}


	private class ServiceInvokeCallable implements Callable<RpcResponse> {

		private RpcRequest request;

		public ServiceInvokeCallable() {

		}

		public ServiceInvokeCallable(RpcRequest request) {
			this.request = request;
		}


		@Override
		public RpcResponse call() throws Exception {
			RpcResponse response = new RpcResponse();
			response.setRequestId(request.getRequestId());
			String serviceName = ServiceSignUtils.sign(request.getService(), request.getMethodName());
			Object target = RpcServiceContext.getRpcTarget(serviceName);
			Class<?> targetClass = Class.forName(request.getService());
			Method method = targetClass.getMethod(request.getMethodName(), request.getParameterTypes());
			Object result = ReflectionUtils.invokeMethod(method, target, request.getParameters());
			response.setResult(result);
			return response;
		}


	}

}
