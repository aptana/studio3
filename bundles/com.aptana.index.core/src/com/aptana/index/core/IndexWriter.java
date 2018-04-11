/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.net.URI;

import com.aptana.jetty.util.epl.ajax.JSON;

/**
 * IndexWriter
 */
public abstract class IndexWriter
{
	/**
	 * Get the URI used as the metadata path
	 * 
	 * @return
	 */
	protected abstract URI getDocumentPath();

	/**
	 * Convert the specified object into a string representation. This representation should be reversible to recreate
	 * the original object
	 * 
	 * @param object
	 * @return
	 */
	protected String serialize(Object object)
	{
		return JSON.toString(object);
	}
}
