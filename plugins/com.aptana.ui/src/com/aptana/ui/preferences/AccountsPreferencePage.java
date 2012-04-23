/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class AccountsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	public static final String ID = "com.aptana.ui.accountsPreferencePage"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = "accountsPreferencePage"; //$NON-NLS-1$
	private static final String ELEMENT_PROVIDER = "provider"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private List<IAccountPageProvider> providers;

	public void init(IWorkbench workbench)
	{
		// processes the extension point
		if (providers != null)
		{
			return;
		}
		providers = new ArrayList<IAccountPageProvider>();

		EclipseUtil.processConfigurationElements(UIPlugin.PLUGIN_ID, EXTENSION_POINT_ID,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						String name = element.getName();
						if (ELEMENT_PROVIDER.equals(name))
						{
							String className = element.getAttribute(ATTR_CLASS);
							if (StringUtil.isEmpty(className))
							{
								return;
							}
							try
							{
								Object provider = element.createExecutableExtension(ATTR_CLASS);
								if (provider instanceof IAccountPageProvider)
								{
									providers.add((IAccountPageProvider) provider);
								}
								else
								{
									IdeLog.logError(UIPlugin.getDefault(), MessageFormat.format(
											"The class {0} does not implement IAccountPageProvider", className)); //$NON-NLS-1$
								}
							}
							catch (CoreException e)
							{
								IdeLog.logError(UIPlugin.getDefault(), e);
							}
						}
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_PROVIDER);
					}
				});

		Collections.sort(providers, new Comparator<IAccountPageProvider>()
		{

			public int compare(IAccountPageProvider provider1, IAccountPageProvider provider2)
			{
				return provider1.getPriority() - provider2.getPriority();
			}
		});
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		for (IAccountPageProvider provider : providers)
		{
			Control control = provider.createContents(main);
			control.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		}
		return main;
	}

	@Override
	public boolean performOk()
	{
		for (IAccountPageProvider provider : providers)
		{
			if (!provider.performOk())
			{
				return false;
			}
		}
		return true;
	}
}
