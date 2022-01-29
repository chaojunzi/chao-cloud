package com.chao.cloud.common.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.VoidFunc0;

/**
 * 关于锁的一些方法
 * 
 * @author 薛超
 * @since 2022年1月29日
 * @version 1.0.0
 */
public final class LockFuncUtil {

	/**
	 * 构造可重入锁
	 * 
	 * @param fair 是否公平
	 * @return 锁
	 */
	public static ReentrantLock buildReentrantLock(boolean fair) {
		return new ReentrantLock(fair);
	}

	/**
	 * 业务锁
	 * 
	 * @param <R>  返回值类型
	 * @param lock 锁
	 * @param func 业务代码
	 * @param time 锁超时
	 * @param unit 锁单位
	 * @return 返回值
	 */
	public static <R> R tryLock(Lock lock, Func0<R> func, long time, TimeUnit unit) {
		return tryLock(lock, func, time, unit, null);
	}

	/**
	 * 业务锁
	 * 
	 * @param <R>  返回值类型
	 * @param lock 锁
	 * @param func 业务代码
	 * @param time 锁超时
	 * @param unit 锁单位
	 * @return 返回值
	 */
	public static <R> R tryLock(Lock lock, Func0<R> func, long time, TimeUnit unit, VoidFunc0 lockAfterFunc) {
		// 加锁前逻辑
		boolean hasLock = false;
		try {
			hasLock = lock.tryLock(time, unit);
			if (hasLock) {// 得到锁
				if (lockAfterFunc != null) {
					lockAfterFunc.call();
				}
				return func.call();
			}
			return null;
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		} finally {
			if (hasLock) {
				lock.unlock();
			}
		}
	}

}
