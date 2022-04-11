package com.ggggght.agent.bytecode;

import com.ggggght.agent.bytecode.domain.OpInfo;

/**
 * 字节码增强抽象
 *
 * @author autorun
 */
public interface EnhancerFactory<T, R> {

	default EnhancerFactory<T, R> getInstance() {
		return this;
	}

	default R insertBefore(ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return opInfo.optional(t -> null);
	}

	default R insertLine(int lineNo, ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return opInfo.optional(t -> null);
	}

	default R insertAfter(ClassLoader loader, String className, Class<?> clazz, OpInfo<T, R> opInfo) {
		return opInfo.optional(t -> null);
	}
}
