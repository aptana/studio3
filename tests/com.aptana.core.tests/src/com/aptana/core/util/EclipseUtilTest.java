/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;

public class EclipseUtilTest extends TestCase {

	public void testGetApplicationLauncher() {
		IPath path = EclipseUtil.getApplicationLauncher();
		assertNotNull(path);
		assertTrue("Eclipse".equalsIgnoreCase(path.removeFileExtension().lastSegment())
				|| "AptanaStudio3".equalsIgnoreCase(path.removeFileExtension().lastSegment())
				|| "Aptana Studio 3".equalsIgnoreCase(path.removeFileExtension().lastSegment()));
	}
}
