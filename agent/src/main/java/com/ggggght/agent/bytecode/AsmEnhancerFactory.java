package com.ggggght.agent.bytecode;

import com.ggggght.agent.bytecode.domain.OpInfo;

public class AsmEnhancerFactory<T, R> extends AbstractEnhancerFactory<T, R> {

	@Override
	public R insertBefore(ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return this.doEnhancer(loader, className, clazz, opInfo);
	}

	@Override
	public R insertLine(int lineNo, ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return this.doEnhancer(loader, className, clazz, opInfo);
	}

	@Override
	public R insertAfter(ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return this.doEnhancer(loader, className, clazz, opInfo);
	}

	@Override
	protected R doEnhancer(ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return opInfo.optional(t -> null);
	}
}
