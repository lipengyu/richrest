package com.googlecode.richrest.server.action;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.googlecode.richrest.server.ActionContext;
import com.googlecode.richrest.server.util.ActionMethodUtils;
import com.googlecode.richrest.util.ClassUtils;

/**
 * 按函数分派Action实现
 * @author <a href="mailto:liangfei0201@gmail.com">liangfei</a>
 */
public abstract class DispatchAction<M extends Serializable> extends AbstractAction<M, Serializable> {

	public String getPage() {
		try {
			String methodName = getMethodName();
			Method method = ClassUtils.getMethod(getClass(), methodName);
			if (method.isAnnotationPresent(Page.class))
				return method.getAnnotation(Page.class).value();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return super.getPage() + ActionMethodUtils.getMethodSeparator() + ActionMethodUtils.getMethodName(ActionContext.getContext().getActionName());
	}

	public Serializable execute(M model) throws Exception {
		String methodName = getMethodName();
		Method method = ClassUtils.getMethod(getClass(), methodName);
		Serializable result = model;
		result = (Serializable)method.invoke(this, model);
		return result;
	}

	protected String getMethodName() {
		return ActionMethodUtils.getMethodName(ActionContext.getContext().getActionName());
	}

}
