package org.eclipse.e4.ui.internal.services;

import org.eclipse.e4.core.services.context.spi.IContextConstants;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.core.services.context.spi.ContextFunction;
import org.eclipse.e4.ui.services.IServiceConstants;

public class ActiveContextsFunction extends ContextFunction {

	@Override
	public Object compute(IEclipseContext context, Object[] arguments) {
		IEclipseContext childContext = (IEclipseContext) context
				.getLocal(IServiceConstants.ACTIVE_CHILD);
		if (childContext != null && arguments.length == 0) {
			return childContext.get(IServiceConstants.ACTIVE_CONTEXTS);
		}
		Set<String> rc = null;
		if (arguments.length == 0) {
			rc = new HashSet<String>();
		} else {
			rc = (Set<String>) arguments[0];
		}
		Set<String> locals = (Set<String>) context
				.getLocal(ContextContextService.LOCAL_CONTEXTS);
		if (locals != null) {
			rc.addAll(locals);
		}
		IEclipseContext parent = (IEclipseContext) context
				.get(IContextConstants.PARENT);
		if (parent != null) {
			parent.get(IServiceConstants.ACTIVE_CONTEXTS,
					new Object[] { rc });
		}
		return rc;
	}

}