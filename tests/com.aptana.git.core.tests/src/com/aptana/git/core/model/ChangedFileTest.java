/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.git.core.model;

import org.junit.Assert;
import org.junit.Test;

import com.aptana.git.core.model.ChangedFile.Status;

public class ChangedFileTest
{

	@Test
	public void testAbsolutePath()
	{
		ChangedFile changedFile = ChangedFile.createInstance("/path/to/file", Status.DELETED);
		Assert.assertEquals(null, changedFile);

		// If this snippet runs on non-Windows, it will fail.
		// changedFile = ChangedFile.createInstance("C:/path/to/file", Status.MODIFIED);
		// Assert.assertEquals(null, changedFile);
	}

	@Test
	public void testRelativePath()
	{
		ChangedFile changedFile = ChangedFile.createInstance("path/to/file", Status.DELETED);
		Assert.assertNotEquals(null, changedFile);

		changedFile = ChangedFile.createInstance("path\\to\\file", Status.MODIFIED);
		Assert.assertNotEquals(null, changedFile);

	}

}
