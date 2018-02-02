package com.rpcframework.core.heartbeat;

import com.rpcframework.core.RpcClientBootstrapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wei.chen1
 * @since 2018/1/26
 */
public class ReconnectProcessor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);

	private ScheduledFuture<?> scheduledFuture;

	private String host;

	private int port;

	public ReconnectProcessor(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void reconnect() {
		scheduledFuture = scheduledExecutor.scheduleAtFixedRate
				(new ReconnectRunnable(this), 0, 5, TimeUnit.SECONDS);
	}

	public void cancel(boolean isSuccess) {
		if (isSuccess) {
			logger.debug("重连成功");
		} else {
			logger.debug("重连3次依然失败");
			RpcClientBootstrapContext context = RpcClientBootstrapContext.getInstance();
			context.addFailureChannel(host, port);
			context.removeChannel(host, port);
		}
		scheduledFuture.cancel(false);
	}


	private class ReconnectRunnable implements Runnable {

		private int reconnCnt = 1;

		private ReconnectProcessor processor;

		public ReconnectRunnable(ReconnectProcessor processor) {
			this.processor = processor;
		}

		@Override
		public void run() {
			if (reconnCnt > 3) {
				cancel(false);
				return;
			}
			logger.debug("重连...第{}次", reconnCnt);
			RpcClientBootstrapContext context = RpcClientBootstrapContext.getInstance();
			context.getPooledChannelHolder(host, port).reconnectChannel(processor);
			reconnCnt++;
		}
	}
}
