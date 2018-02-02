package com.rpcframework.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wei.chen1
 * @since 2018/2/1
 */
public class TransceiverLock {

	private ReentrantLock lock;

	private Condition condition;

	public TransceiverLock() {
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
	}

	public void lock(){
		this.lock.lock();
	}

	public void await() throws InterruptedException {
		this.condition.await();
	}

	public void unlock(){
		this.lock.unlock();
	}

	public void signal(){
		this.condition.signal();
	}
}
