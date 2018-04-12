/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.index;

/**
 * KeyProvider
 */
public class KeyProvider implements IKeyProvider
{
	protected static final String ATTRIBUTE = "attribute"; //$NON-NLS-1$
	protected static final String ELEMENT = "element"; //$NON-NLS-1$

	private String _prefix;

	/**
	 * KeyProvider
	 * 
	 * @param prefix
	 */
	public KeyProvider(String prefix)
	{
		this._prefix = prefix;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.contentassist.index.IKeyProvider#getAttributeKey()
	 */
	public String getAttributeKey()
	{
		return this._prefix + ATTRIBUTE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.contentassist.index.IKeyProvider#getElementKey()
	 */
	public String getElementKey()
	{
		return this._prefix + ELEMENT;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.contentassist.index.IKeyProvider#getMetadataLocation()
	 */
	public String getMetadataLocation()
	{
		return "metadata:/xml"; //$NON-NLS-1$
	}
}
