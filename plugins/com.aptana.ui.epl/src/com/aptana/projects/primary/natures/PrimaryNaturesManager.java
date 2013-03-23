/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.primary.natures;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.epl.UIEplPlugin;

/**
 * @author pinnamuri
 */
public class PrimaryNaturesManager
{

	private final String EXTENSION_POINT = "primaryNatureContributors";//$NON-NLS-1$
	private final String ELEMENT_CONTRIBUTOR = "contributor";//$NON-NLS-1$
	private final String ELEMENT_NATURE_ID = "natureId";//$NON-NLS-1$
	private final String ELEMENT_CLASS = "class";//$NON-NLS-1$

	private static PrimaryNaturesManager INSTANCE;
	private Map<String, IPrimaryNatureContributor> natureIdRanks;

	public synchronized static PrimaryNaturesManager getManager()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new PrimaryNaturesManager();
		}

		return INSTANCE;
	}

	private PrimaryNaturesManager()
	{
	}

	private void readExtensionRegistry()
	{
		EclipseUtil.processConfigurationElements(UIEplPlugin.PLUGIN_ID, EXTENSION_POINT,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						readElement(element);
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_CONTRIBUTOR);
					}
				});
	}

	private void readElement(IConfigurationElement element)
	{
		if (ELEMENT_CONTRIBUTOR.equals(element.getName()))
		{
			String contributorClass = element.getAttribute(ELEMENT_CLASS);
			if (!StringUtil.isEmpty(contributorClass))
			{
				try
				{
					IPrimaryNatureContributor natureRankContributor = (IPrimaryNatureContributor) element
							.createExecutableExtension(ELEMENT_CLASS);
					String natureId = element.getAttribute(ELEMENT_NATURE_ID);

					natureIdRanks.put(natureId, natureRankContributor);
				}
				catch (CoreException e)
				{
					// ignores the exception since it's optional
				}
			}
		}
	}

	public Map<String, IPrimaryNatureContributor> getContributorsMap()
	{
		if (natureIdRanks == null)
		{
			natureIdRanks = new HashMap<String, IPrimaryNatureContributor>();
			readExtensionRegistry();
		}
		return natureIdRanks;
	}

}
