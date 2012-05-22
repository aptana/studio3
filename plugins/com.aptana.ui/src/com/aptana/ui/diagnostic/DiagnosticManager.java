/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.diagnostic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class DiagnosticManager
{

	private static final String EXTENSION_NAME = "diagnostic"; //$NON-NLS-1$
	private static final String ELEMENT_LOG = "diagnosticLog"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$
	private static final int DEFAULT_PRIORITY = 50;

	private static DiagnosticManager instance;

	private List<IDiagnosticLog> logs;

	public synchronized static DiagnosticManager getInstance()
	{
		if (instance == null)
		{
			instance = new DiagnosticManager();
		}
		return instance;
	}

	private DiagnosticManager()
	{
		logs = new ArrayList<IDiagnosticLog>();
		loadExtensions();
	}

	public List<IDiagnosticLog> getLogs()
	{
		return Collections.unmodifiableList(logs);
	}

	private void loadExtensions()
	{
		final List<DiagnosticLog> result = new ArrayList<DiagnosticLog>();

		EclipseUtil.processConfigurationElements(UIPlugin.PLUGIN_ID, EXTENSION_NAME,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						String classStr = element.getAttribute(ATTR_CLASS);
						if (StringUtil.isEmpty(classStr))
						{
							return;
						}
						IDiagnosticLog logClass = null;
						try
						{
							Object clazz = element.createExecutableExtension(ATTR_CLASS);
							if (clazz instanceof IDiagnosticLog)
							{
								logClass = (IDiagnosticLog) clazz;
							}
							else
							{
								IdeLog.logError(UIPlugin.getDefault(), MessageFormat.format(
										"The class {0} does not implement IDiagnosticLog.", classStr)); //$NON-NLS-1$
							}
						}
						catch (CoreException e)
						{
							IdeLog.logError(UIPlugin.getDefault(), e);
						}
						if (logClass == null)
						{
							return;
						}

						int priority = DEFAULT_PRIORITY;
						String priorityStr = element.getAttribute(ATTR_PRIORITY);
						if (!StringUtil.isEmpty(priorityStr))
						{
							try
							{
								priority = Integer.parseInt(priorityStr);
							}
							catch (NumberFormatException e)
							{
								IdeLog.logWarning(UIPlugin.getDefault(),
										"The priority for diagnosticLog needs to be an integer.", e); //$NON-NLS-1$
							}
						}
						result.add(new DiagnosticLog(logClass, priority));
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_LOG);
					}
				});

		Collections.sort(result, new Comparator<DiagnosticLog>()
		{

			public int compare(DiagnosticLog arg0, DiagnosticLog arg1)
			{
				return arg0.priority - arg1.priority;
			}

		});
		for (DiagnosticLog log : result)
		{
			logs.add(log.logClass);
		}
	}

	private static class DiagnosticLog
	{
		public final IDiagnosticLog logClass;
		public final int priority;

		private DiagnosticLog(IDiagnosticLog logClass, int priority)
		{
			this.logClass = logClass;
			this.priority = priority;
		}
	}
}
