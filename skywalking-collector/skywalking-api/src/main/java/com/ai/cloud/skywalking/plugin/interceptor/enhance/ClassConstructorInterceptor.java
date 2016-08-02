package com.ai.cloud.skywalking.plugin.interceptor.enhance;

import com.ai.cloud.skywalking.logging.LogManager;
import com.ai.cloud.skywalking.logging.Logger;
import com.ai.cloud.skywalking.plugin.interceptor.loader.InterceptorInstanceLoader;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import com.ai.cloud.skywalking.plugin.interceptor.EnhancedClassInstanceContext;

public class ClassConstructorInterceptor {
	private static Logger logger = LogManager
			.getLogger(ClassConstructorInterceptor.class);

	private String instanceMethodsAroundInterceptorClassName;

	public ClassConstructorInterceptor(String instanceMethodsAroundInterceptorClassName) {
		this.instanceMethodsAroundInterceptorClassName = instanceMethodsAroundInterceptorClassName;
	}

	@RuntimeType
	public void intercept(
			@This Object obj,
			@FieldProxy(ClassEnhancePluginDefine.contextAttrName) FieldSetter accessor,
			@AllArguments Object[] allArguments) {
		try {
			InstanceMethodsAroundInterceptor interceptor = InterceptorInstanceLoader.load(instanceMethodsAroundInterceptorClassName, obj.getClass().getClassLoader());

			EnhancedClassInstanceContext context = new EnhancedClassInstanceContext();
			accessor.setValue(context);
			ConstructorInvokeContext interceptorContext = new ConstructorInvokeContext(obj,
					allArguments);
			interceptor.onConstruct(context, interceptorContext);
		} catch (Throwable t) {
			logger.error("ClassConstructorInterceptor failue.", t);
		}

	}
}
