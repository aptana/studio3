/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

public interface BundleProcessor
{
	/**
	 * processBundle
	 * 
	 * @param bundle
	 * @return Returns true if processing should continue
	 */
	public boolean processBundle(BundleEntry entry, BundleElement bundle);
}
