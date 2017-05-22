/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.diagnostic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.CorePlugin;
import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.core.diagnostic.IDiagnosticManager;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class DiagnosticManager implements IDiagnosticManager
{

	private static final String EXTENSION_NAME = "diagnostic"; //$NON-NLS-1$
	private static final String ELEMENT_LOG = "diagnosticLog"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$
	private static final int DEFAULT_PRIORITY = 50;

	private List<IDiagnosticLog> logs;

	public DiagnosticManager()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.diagnostic.IDiagnosticManager#getLogs()
	 */
	public List<IDiagnosticLog> getLogs()
	{
		loadExtensions();
		return logs;
	}

	/**
	 * Lazily load the extensions.
	 */
	private synchronized void loadExtensions()
	{
		if (logs != null)
		{
			return;
		}

		final ArrayList<LazyDiagnosticLog> result = new ArrayList<LazyDiagnosticLog>();
		EclipseUtil.processConfigurationElements(CorePlugin.PLUGIN_ID, EXTENSION_NAME,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						String classStr = element.getAttribute(ATTR_CLASS);
						if (StringUtil.isEmpty(classStr))
						{
							return;
						}

						result.add(new LazyDiagnosticLog(element));
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_LOG);
					}
				});

		Collections.sort(result, new Comparator<LazyDiagnosticLog>()
		{
			public int compare(LazyDiagnosticLog arg0, LazyDiagnosticLog arg1)
			{
				return arg0.getPriority() - arg1.getPriority();
			}
		});
		logs = Collections.unmodifiableList(new ArrayList<IDiagnosticLog>(result));
	}

	/**
	 * This lazily loads the actual implementation only when needed, and reads priorities withotu loading the actual
	 * class.
	 * 
	 * @author cwilliams
	 */
	private static class LazyDiagnosticLog implements IDiagnosticLog
	{
		public IDiagnosticLog logClass;
		public Integer priority;
		private IConfigurationElement element;

		private LazyDiagnosticLog(IConfigurationElement element)
		{
			this.element = element;
		}

		private synchronized int getPriority()
		{
			if (priority == null)
			{
				priority = DEFAULT_PRIORITY;
				String priorityStr = element.getAttribute(ATTR_PRIORITY);
				if (!StringUtil.isEmpty(priorityStr))
				{
					try
					{
						priority = Integer.parseInt(priorityStr);
					}
					catch (NumberFormatException e)
					{
						IdeLog.logWarning(CorePlugin.getDefault(),
								"The priority for diagnosticLog needs to be an integer.", e); //$NON-NLS-1$
					}
				}
			}
			return priority;
		}

		private synchronized IDiagnosticLog getWrapped()
		{
			if (logClass == null)
			{
				logClass = new NullDiagnosticLog(); // default to null impl
				try
				{
					Object clazz = element.createExecutableExtension(ATTR_CLASS);
					if (clazz instanceof IDiagnosticLog)
					{
						logClass = (IDiagnosticLog) clazz;
					}
					else
					{
						IdeLog.logWarning(CorePlugin.getDefault(), MessageFormat.format(
								"The class {0} does not implement IDiagnosticLog.", element.getAttribute(ATTR_CLASS))); //$NON-NLS-1$
					}
				}
				catch (CoreException e)
				{
					IdeLog.logError(CorePlugin.getDefault(), e);
				}
			}
			return logClass;
		}

		public String getLog()
		{
			return getWrapped().getLog();
		}
	}

	/**
	 * When we can't properly load an IDiagnosticLog for some reason, this is returned in it's place.
	 * 
	 * @author cwilliams
	 */
	private static final class NullDiagnosticLog implements IDiagnosticLog
	{
		public String getLog()
		{
			return StringUtil.EMPTY;
		}
	}
}
