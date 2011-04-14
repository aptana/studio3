/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.ui.IDiagnosticLog;
import com.aptana.ui.UIPlugin;
import com.aptana.ui.dialogs.DiagnosticDialog;
import com.aptana.ui.util.UIUtils;

public class DiagnosticHandler extends AbstractHandler
{

	private static final String EXTENSION_NAME = "diagnostic"; //$NON-NLS-1$
	private static final String EXTENSION_POINT = UIPlugin.PLUGIN_ID + "." + EXTENSION_NAME; //$NON-NLS-1$
	private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
	private static final String ORDINAL_ATTRIBUTE = "ordinal"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Job job = new Job("Getting Diagnostic Logs") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				final String content = getLogContent();
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						DiagnosticDialog dialog = new DiagnosticDialog(UIUtils.getActiveShell());
						dialog.open();
						dialog.append(content);
					}

				});
				return Status.OK_STATUS;
			}

		};
		job.setSystem(true);
		job.schedule();
		return null;
	}

	public static String getLogContent()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);
		// sorts the extension points by the ordinal field
		Map<Integer, List<IConfigurationElement>> ordinalElements = new TreeMap<Integer, List<IConfigurationElement>>();
		List<IConfigurationElement> elementList;
		List<IConfigurationElement> otherElements = new ArrayList<IConfigurationElement>();
		String ordinalAttr;
		int ordinal;
		for (IConfigurationElement element : elements)
		{
			if (element.getName().equals(EXTENSION_NAME))
			{
				ordinalAttr = element.getAttribute(ORDINAL_ATTRIBUTE);
				if (ordinalAttr != null)
				{
					try
					{
						ordinal = Integer.parseInt(ordinalAttr);
						elementList = ordinalElements.get(ordinal);
						if (elementList == null)
						{
							elementList = new ArrayList<IConfigurationElement>();
							ordinalElements.put(ordinal, elementList);
						}
						elementList.add(element);
						continue;
					}
					catch (NumberFormatException e)
					{
					}
				}
				otherElements.add(element);
			}
		}
		List<IConfigurationElement> list = new ArrayList<IConfigurationElement>();
		for (Integer key : ordinalElements.keySet())
		{
			list.addAll(ordinalElements.get(key));
		}
		list.addAll(otherElements);

		StringBuilder content = new StringBuilder();
		String className;
		for (IConfigurationElement element : list)
		{
			className = element.getAttribute(CLASS_ATTRIBUTE);
			if (className != null)
			{
				try
				{
					Object client = element.createExecutableExtension(CLASS_ATTRIBUTE);
					if (client instanceof IDiagnosticLog)
					{
						String log = ((IDiagnosticLog) client).getLog();
						if (log != null && log.length() > 0)
						{
							content.append(log);
							content.append("\n"); //$NON-NLS-1$
						}
					}
				}
				catch (CoreException e)
				{
				}
			}
		}
		return content.toString();
	}

}
