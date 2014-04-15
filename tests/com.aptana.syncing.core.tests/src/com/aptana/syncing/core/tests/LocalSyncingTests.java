/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.core.tests;

import java.io.File;

import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.LocalConnectionPoint;

public class LocalSyncingTests extends SyncingTests
{

	@Override
	public void setUp() throws Exception
	{
		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		baseTempFile.deleteOnExit();

		File baseDirectory = baseTempFile.getParentFile();

		LocalConnectionPoint lcp = new LocalConnectionPoint();
		lcp.setPath(new Path(baseDirectory.getAbsolutePath()));
		clientManager = lcp;

		LocalConnectionPoint scp = new LocalConnectionPoint();
		scp.setPath(new Path(baseDirectory.getAbsolutePath()));
		serverManager = scp;

		super.setUp();
	}
}
