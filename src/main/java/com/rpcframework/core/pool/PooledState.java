package com.rpcframework.core.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wei.chen1
 * @since 2018/2/2
 */
public class PooledState {

	private final List<PooledChannel> idelChannelList = new ArrayList<>();
	private final List<PooledChannel> activeChannelList = new ArrayList<>();

	private final ReentrantLock lock = new ReentrantLock();
	private final Condition lockCondition = lock.newCondition();
	private final Condition activeCondition = lock.newCondition();


	public List<PooledChannel> getIdelChannelList() {
		return idelChannelList;
	}

	public void addIdelChannel(PooledChannel pooledChannel) {
		idelChannelList.add(pooledChannel);
	}

	public void removeIdelChannel(PooledChannel pooledChannel) {
		idelChannelList.remove(pooledChannel);
	}

	public List<PooledChannel> getActiveChannelList() {
		return activeChannelList;
	}

	public void addActiveChannel(PooledChannel pooledChannel) {
		activeChannelList.add(pooledChannel);
	}

	public void removeActiveChannel(PooledChannel pooledChannel) {
		activeChannelList.add(pooledChannel);
	}

	public boolean isIdelChannelListEmpty(){
		return idelChannelList.isEmpty();
	}

	public PooledChannel getIdelChannel(){
		return idelChannelList.remove(0);
	}

	public int activeChannelSize(){
		return activeChannelList.size();
	}

	public int idleChannelSize(){
		return idelChannelList.size();
	}

	public void lock(){
		this.lock.lock();
	}

	public void unlock(){
		this.lock.unlock();
	}

	public void await() throws InterruptedException {
		this.lockCondition.await();
	}

	public void signal(){
		this.lockCondition.signal();
	}

	public void waitIdle() throws InterruptedException {
		this.activeCondition.await();
	}

	public void signIdle(){
		this.activeCondition.signal();
	}

	public void removeAllChannel(){
		this.idelChannelList.removeAll(idelChannelList);
		this.activeChannelList.removeAll(activeChannelList);
	}
}
