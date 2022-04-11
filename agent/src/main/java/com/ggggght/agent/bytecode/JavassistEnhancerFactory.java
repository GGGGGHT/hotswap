package com.ggggght.agent.bytecode;

import com.ggggght.agent.bytecode.domain.OpInfo;

public class JavassistEnhancerFactory<T, R> extends AbstractEnhancerFactory<T, R> {

	@Override
	protected R doEnhancer(ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return opInfo.optional(t -> null);
	}
}
