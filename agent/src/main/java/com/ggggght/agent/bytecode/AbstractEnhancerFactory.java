package com.ggggght.agent.bytecode;

import com.ggggght.agent.bytecode.domain.OpInfo;

/**
 * @
 * @param <T>
 * @param <R>
 */
public abstract class AbstractEnhancerFactory<T, R> implements EnhancerFactory<T, R> {

	@Override
	public EnhancerFactory<T, R> getInstance() {
		return this;
	}

	protected abstract R doEnhancer(ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo);

}
