/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

public abstract class ContributedExtension implements IContributedExtension, IExecutableExtension
{

	private String description;
	private String id;
	private String name;
	private String contentType;
	private String propertyPageId;
	private String preferencePageId;
	private int priority;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IContributedExtension#getDescription()
	 */
	public String getDescription()
	{
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IContributedExtension#getId()
	 */
	public String getId()
	{
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IContributedExtension#getName()
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IContributedExtension#getContentType()
	 */
	public String getContentType()
	{
		return contentType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IContributedExtension#getPreferencePageId()
	 */
	public String getPreferencePageId()
	{
		return preferencePageId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IContributedExtension#getPriority()
	 */
	public int getPriority()
	{
		return priority;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IContributedExtension#getPropertyPageId()
	 */
	public String getPropertyPageId()
	{
		return propertyPageId;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement
	 * , java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
	{
		id = config.getAttribute(ID);
		name = config.getAttribute(NAME);
		description = config.getAttribute(DESCRIPTION);
		priority = Integer.parseInt(config.getAttribute(PRIORITY));

		propertyPageId = config.getAttribute(PROP_PAGE_ID);
		preferencePageId = config.getAttribute(PREF_PAGE_ID);

		// get the contentType from the parent
		final Object parent = config.getParent();
		if (parent instanceof IConfigurationElement)
			contentType = ((IConfigurationElement) parent).getAttribute(CONTENT_TYPE);
	}
}
