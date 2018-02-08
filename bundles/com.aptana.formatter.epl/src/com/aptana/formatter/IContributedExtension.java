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

/**
 * Interface defining methods that should be exposed by an extension implementation that may have more then one
 * implemenation defined for it.
 * <p>
 * Users will be able select which implemenation is used via an extension specific preference/property page. Examples:
 * <ul>
 * <li>Source Parsers
 * <li>Debugging Engines
 * </ul>
 * </p>
 */
public interface IContributedExtension
{

	static final String ID = "id"; //$NON-NLS-1$
	static final String NAME = "name"; //$NON-NLS-1$
	static final String DESCRIPTION = "description"; //$NON-NLS-1$
	static final String PREF_PAGE_ID = "preferencePageId"; //$NON-NLS-1$
	static final String PROP_PAGE_ID = "propertyPageId"; //$NON-NLS-1$
	static final String PRIORITY = "priority"; //$NON-NLS-1$
	static final String CONTENT_TYPE = "contentType"; //$NON-NLS-1$

	/**
	 * Returns the contribution id
	 */
	String getId();

	/**
	 * Returns the contribution nature id
	 */
	String getContentType();

	/**
	 * Returns the contribution name
	 */
	String getName();

	/**
	 * Returns the contribution description
	 */
	String getDescription();

	/**
	 * Returns the contribution's preference page id, or <code>null</code> if one has not been set.
	 */
	String getPreferencePageId();

	/**
	 * Returns the contributions property page id, or <code>null</code> if one has not been set.
	 */
	String getPropertyPageId();

	/**
	 * Returns the contribution priority
	 */
	int getPriority();
}