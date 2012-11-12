/**
 * 
 */
package com.android.helpme.demo.utils;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Andreas Wieland
 *
 */
public class ThreadPool {
	int coreSize;
	int maxPoolSize;
	ThreadPoolExecutor threadPoolExecutor = null;
	ArrayBlockingQueue<Runnable> workQueue;
	private static ThreadPool threadPool;
	private ThreadPool(int keepAliveTime) {
		Runtime runtime = Runtime.getRuntime();
		coreSize = runtime.availableProcessors();
		maxPoolSize = coreSize * 2;
		workQueue = new ArrayBlockingQueue<Runnable>(maxPoolSize * 10);
		threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
	}
	
	public static ThreadPool getThreadPool(int keepAliveTime) {
		if (threadPool == null) {
			threadPool = new ThreadPool(keepAliveTime);
		}return threadPool;
	}
	
	public static void runTask(Runnable runnable){
		threadPool.threadPoolExecutor.execute(runnable);
	}
	
	public static void runTasks(ArrayList<Runnable> runnables) {
		for (Runnable runnable : runnables) {
			threadPool.threadPoolExecutor.execute(runnable);
		}
	}
	public static void shutdown(){
		threadPool.threadPoolExecutor.shutdown();
	}
}
