/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.net.URI;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;

/**
 * IIndexFileContributor
 */
public interface IIndexFileContributor
{
	Set<IFileStore> getFiles(URI container);
}
