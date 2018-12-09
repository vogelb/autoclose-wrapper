package de.github.vogelb.autoclose;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AutoCloseWrapper implements InvocationHandler, AutoCloseable {
	private enum CloseMechanism {
		reflection, callback
	}

	public interface CloseCallback<T> {
		void close(T delegate);
	}
	
	private final Object delegate;
	
	private final Method closeMethod;
	
	private final Object[] closeArgs;
	
	@SuppressWarnings("rawtypes")
	private final CloseCallback closeCallback;
	
	private final CloseMechanism mechanism;
	
	private AutoCloseWrapper(Object aDelegate) {
		this (aDelegate, "close");
	}
	
	private AutoCloseWrapper(Object aDelegate, String closeMethodName) {
		mechanism = CloseMechanism.reflection;
		delegate = aDelegate;
		closeCallback = null;
		closeArgs = null;
		try {
			closeMethod = delegate.getClass().getMethod(closeMethodName);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Close method " + closeMethodName +  "(void) not found.");
		}
	}
	
	private AutoCloseWrapper(Object aDelegate, Method aCloseMethod, Object... args) {
		mechanism = CloseMechanism.reflection;
		delegate = aDelegate;
		closeCallback = null;
		closeMethod = aCloseMethod;
		closeArgs = args;
	}
	
	private AutoCloseWrapper(Object aDelegate, CloseCallback<?> aCallback) {
		mechanism = CloseMechanism.callback;
		delegate = aDelegate;
		closeCallback = aCallback;
		closeMethod = null;
		closeArgs = null;
	}
	
	@SuppressWarnings("unchecked")
	public static <P, D> P createInstance(Class<P> aProxyInterface, D aDelegate, CloseCallback<D> callback) {
		return (P) Proxy.newProxyInstance(aDelegate.getClass().getClassLoader(), 
										  new Class[] { aProxyInterface }, 
										  new AutoCloseWrapper(aDelegate, callback));
	}
	
	@SuppressWarnings("unchecked")
	public static <P> P createInstance(Class<P> aProxyInterface, Object aDelegate, String aCloseMethod) {
		return (P) Proxy.newProxyInstance(aDelegate.getClass().getClassLoader(), 
										  new Class[] { aProxyInterface }, 
										  new AutoCloseWrapper(aDelegate, aCloseMethod));
	}
	
	@SuppressWarnings("unchecked")
	public static <P> P createInstance(Class<P> aProxyInterface, Object aDelegate, Method aCloseMethod, Object... args) {
		return (P) Proxy.newProxyInstance(aDelegate.getClass().getClassLoader(), 
										  new Class[] { aProxyInterface }, 
										  new AutoCloseWrapper(aDelegate, aCloseMethod, args));
	}
	
	@SuppressWarnings("unchecked")
	public static <P> P createInstance(Class<P> aProxyInterface, Object aDelegate) {
		return (P) Proxy.newProxyInstance(aDelegate.getClass().getClassLoader(), 
										  new Class[] { aProxyInterface }, 
										  new AutoCloseWrapper(aDelegate));
	}
	
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// Prefer wrapped close() method even if delegate implements close()
		if (method.getName().equals("close") && args == null) {
			close();
			return null;
		}
		return method.invoke(delegate, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void close() {
		switch (mechanism) {
			case callback:
				closeCallback.close(delegate);
				break;
			case reflection:
				try {
					closeMethod.invoke(delegate, closeArgs);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("Error closing instance of "  + delegate.getClass().getName() +  " using method " + closeMethod.getName());
				}
				break;
			default:
				throw new IllegalStateException("Unkniwn close mechanism " + mechanism);
		}
	}
	
}
