package com.ggggght.agent.bytecode.domain;

import java.util.function.Function;

@FunctionalInterface
public interface OpInfo<T, R> {

	default void preOperator() {

	}

	R optional(Function<T, R> fun);


	default void postOperator() {

	}

}
